package org.wycliffeassociates.otter.common.domain

import io.reactivex.*
import io.reactivex.internal.schedulers.SchedulerPoolFactory
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.data.model.*
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.domain.usfm.ParseUsfm
import org.wycliffeassociates.otter.common.persistence.repositories.*

import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.DublinCore
import org.wycliffeassociates.resourcecontainer.entity.Project
import org.wycliffeassociates.resourcecontainer.errors.RCException

import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.concurrent.Executors

class ImportResourceContainer(
        private val languageRepository: ILanguageRepository,
        private val metadataRepository: IResourceMetadataRepository,
        private val collectionRepository: ICollectionRepository,
        private val chunkRepository: IChunkRepository,
        directoryProvider: IDirectoryProvider
) {
    private val rcDirectory = File(directoryProvider.getAppDataDirectory(), "rc")

    fun import(file: File): Completable {
        return when {
            file.isDirectory -> importDirectory(file)
            else -> Completable.complete()
        }
    }

    private fun importDirectory(dir: File): Completable {
        if (validateResourceContainer(dir)) {
            if (dir.parentFile?.absolutePath != rcDirectory.absolutePath) {
                val success = dir.copyRecursively(File(rcDirectory, dir.name), true)
                if (!success) {
                    throw IOException("Could not copy resource container ${dir.name} to resource container directory")
                }
            }
            return importResourceContainer(File(rcDirectory, dir.name))
        } else {
            return Completable.error(RCException("Missing manifest.yaml"))
        }
    }

    private fun validateResourceContainer(dir: File): Boolean {
        val names = dir.listFiles().map { it.name }
        return names.contains("manifest.yaml")
    }

    private fun importResourceContainer(container: File): Completable {
        val rc = ResourceContainer.load(container)
        val dc = rc.manifest.dublinCore



        return Completable.fromAction {
            if (dc.type == "bundle" && dc.format == "text/usfm") {
                expandResourceContainerBundle(rc)
            }
        }.andThen(languageRepository.getBySlug(dc.language.identifier).map {
            dc.mapToMetadata(container, it)
        }.flatMap {
            val resourceMetadata = it
            //metadata id is going to be needed for the collection insert
            return@flatMap metadataRepository.insert(resourceMetadata).map {
                resourceMetadata.id = it
                return@map resourceMetadata
            }
        }.flatMapCompletable {
            val resourceMetadata = it

            val oldTestamentBooks = mutableListOf<RelatedCollectionContent>()
            val newTestamentBooks = mutableListOf<RelatedCollectionContent>()

            for (project in rc.manifest.projects) {
                val projCollection = project.mapToCollection(resourceMetadata.type, resourceMetadata)
                val chapters = mutableListOf<RelatedCollectionContent>()
                // Get all the chapters
                val projectsDir = File(resourceMetadata.path, project.path)
                val files = projectsDir.listFiles(FileFilter { it.extension == "usfm" })
                for (file in files) {
                    val usfmDoc = ParseUsfm(file).parse()
                    for (chapter in usfmDoc.chapters.toList()) {
                        val chapCollection = Collection(
                                chapter.first,
                                "${resourceMetadata.language.slug}_${projCollection.slug}_ch${chapter.first}",
                                "chapter",
                                chapter.first.toString(),
                                resourceMetadata
                        )

                        // Get all the verses
                        val verses = mutableListOf<Chunk>()
                        for (verse in chapter.second.values) {
                            val verseChunk = Chunk(
                                    verse.number,
                                    "verse",
                                    verse.number,
                                    verse.number,
                                    null
                            )
                            verses.add(verseChunk)
                        }

                        val relatedChapter = RelatedCollectionContent(
                                chapCollection,
                                listOf(),
                                verses
                        )

                        chapters.add(relatedChapter)
                    }
                }

                val relatedProject = RelatedCollectionContent(
                        projCollection,
                        chapters,
                        listOf()
                )

                // Put with the right testament
                when (project.categories.first()) {
                    "bible-ot" -> oldTestamentBooks.add(relatedProject)
                    "bible-nt" -> newTestamentBooks.add(relatedProject)
                }
            }

            // Create the Bible data
            // TODO: Remove this when Bible, OT, NT are included as part of a resource container
            val oldTestament = RelatedCollectionContent(
                    Collection(1, "bible-ot", "testament", "Old Testament", resourceMetadata),
                    oldTestamentBooks,
                    listOf()
            )
            val newTestament = RelatedCollectionContent(
                    Collection(1, "bible-nt", "testament", "New Testament", resourceMetadata),
                    newTestamentBooks,
                    listOf()
            )
            val root = RelatedCollectionContent(
                    Collection(1, "bible", "bible", "Bible", resourceMetadata),
                    listOf(oldTestament, newTestament),
                    listOf()
            )
            // Insert the root
            return@flatMapCompletable collectionRepository.insertRelatedCollectionContent(root)
        }).subscribeOn(Schedulers.io())
    }

    fun expandResourceContainerBundle(rc: ResourceContainer) {
        val dc = rc.manifest.dublinCore
        dc.type = "book"

        for (project in rc.manifest.projects) {
            expandUsfm(rc.dir, project)
        }

        rc.writeManifest()
    }

    fun expandUsfm(root: File, project: Project) {
        val projectRoot = File(root, project.identifier)
        projectRoot.mkdir()
        val usfmFile = File(root, project.path)
        if (usfmFile.exists() && usfmFile.extension == "usfm") {
            val book = ParseUsfm(usfmFile).parse()
            val chapterPadding = book.chapters.size.toString().length //length of the string version of the number of chapters
            val bookDir = File(root, project.identifier)
            bookDir.mkdir()
            for (chapter in book.chapters.entries) {
                val chapterFile = File(bookDir, chapter.key.toString().padStart(chapterPadding, '0') + ".usfm")
                val verses = chapter.value.entries.map { it.value }.toTypedArray()
                verses.sortBy { it.number }
                chapterFile.bufferedWriter().use {
                    it.write("\\c ${chapter.key}")
                    it.newLine()
                    for (verse in verses) {
                        it.appendln("\\v ${verse.number} ${verse.text}")
                    }
                }
            }
            usfmFile.delete()
        }
        project.path = "./${project.identifier}"
    }
}

private fun Project.mapToCollection(type: String, metadata: ResourceMetadata): Collection {
    return Collection(
            sort,
            identifier,
            type,
            title,
            metadata
    )
}

private fun DublinCore.mapToMetadata(dir: File, lang: Language): ResourceMetadata {
    val (issuedDate, modifiedDate) = listOf(issued, modified)
            .map {
                // String could be in any of [W3 ISO8601 profile](https://www.w3.org/TR/NOTE-datetime)
                // Sanitize to be YYYY-MM-DD
                it
                        // Remove any time information
                        .substringBefore("T")
                        // Split into YYYY, MM, and DD parts
                        .split("-")
                        .toMutableList()
                        // Add any months or days to complete the YYYY-MM-DD format
                        .apply {
                            for (i in 1..(3 - size)) {
                                add("01")
                            }
                        }
                        // Combine back to a string
                        .joinToString("-")
                        // Parse to local date
                        .let { sanitized -> LocalDate.parse(sanitized) }
            }

    return ResourceMetadata(
            conformsTo,
            creator,
            description,
            format,
            identifier,
            issuedDate,
            lang,
            modifiedDate,
            publisher,
            subject,
            type,
            title,
            version,
            dir
    )
}