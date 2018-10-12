package org.wycliffeassociates.otter.common.domain

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.usfm.ParseUsfm
import org.wycliffeassociates.otter.common.domain.usfm.UsfmDocument
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

        if (dc.type == "bundle" && dc.format == "text/usfm") {
            expandResourceContainerBundle(rc)
        }

        return languageRepository.getBySlug(dc.language.identifier).map {
            dc.mapToMetadata(container, it)
        }.flatMap {
            val resourceMetadata = it
            //metadata id is going to be needed for the collection insert
            metadataRepository.insert(resourceMetadata).doOnError { println(it) }.map {
                resourceMetadata.id = it
                resourceMetadata
            }
        }.flatMapCompletable {
            val resourceMetadata = it
            importBible(resourceMetadata)
            Observable.fromIterable(rc.manifest.projects).flatMapCompletable {
                importProject(it, resourceMetadata).doOnError { println(it) }
            }
        }
    }


    fun importBible(meta: ResourceMetadata) {
        //Initialize bible and testament collections
        val bible = Collection(1, "bible", "bible", "Bible", meta)
        val ot = Collection(1, "bible-ot", "testament", "Old Testament", meta)
        val nt = Collection(2, "bible-nt", "testament", "New Testament", meta)
        collectionRepository.insert(bible).blockingGet()
        collectionRepository.insert(ot).blockingGet()
        collectionRepository.insert(nt).blockingGet()
        collectionRepository.updateParent(ot, bible).blockingGet()
        collectionRepository.updateParent(nt, bible).blockingGet()
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

    private fun importProject(p: Project, resourceMetadata: ResourceMetadata): Completable {
        Single.just(p.mapToCollection(resourceMetadata.type, resourceMetadata))
                .flatMap({ book: Collection ->
                    collectionRepository.insert(book)
                }, {
                    book: Collection, id: Single<Int> -> Pair(book, id)
                })
                .map {
                    println("${book.slug} setting id to $it")
                    book.id = it
                    book
                }.map {
                    importChapters(p, it, resourceMetadata).doOnError{ println(it)}.blockingGet()
                    it
                }.flatMapCompletable {
                    val book = it
                    //associate a parent/child relationship with the project if there is a category entry
                    if (p.categories.isNotEmpty()) {
                        collectionRepository.getBySlugAndContainer(p.categories.first(), book.resourceContainer!!)
                                .doOnError { println(it) }
                                .flatMapCompletable {
                                    collectionRepository.updateParent(book, it)
                                            .doOnError { println(it) }
                                            .doOnComplete { println("Updated parent of ${book.slug} with id of ${book.id} to ${it.slug} with id of ${it.id}")}
                                }
                    } else {
                        Completable.complete()
                    }
                }
    }

    private fun importChapters(project: Project, book: Collection, meta: ResourceMetadata): Completable {
        val root = File(meta.path, project.path)
        val files = root.listFiles(FileFilter { it.extension == "usfm" })
        val obs = Observable.fromIterable(files.toList())
        return obs.map {
            //parse each chapter usfm file
            ParseUsfm(it).parse()
        }.flatMap {
            //iterate over each chapter
            Observable.fromIterable(it.chapters.toList())
        }.flatMapSingle {
            // create a collection out of each chapter to store in the database
            val chapter = it
            val ch = Collection(
                    chapter.first,
                    "${meta.language.slug}_${book.slug}_ch${chapter.first}",
                    "chapter",
                    chapter.first.toString(),
                    meta
            )
            collectionRepository.insert(ch).doOnError { println(it) }.map {
                ch.id = it //set the id allocated by the repository
                Pair(chapter, ch) //return both the chapter and the collection
            }
        }.map {
            collectionRepository.updateParent(it.second, book)
                    .doOnError { println(it) }
                    .doOnComplete {
                        println("Updated parent of ${it.second.slug} with id of ${it.second.id} to ${book.slug} with id of ${book.id}")
                    }.subscribe()
            it
        }.flatMap {
            val chapter = it.first
            val chapterCollection = it.second
            Observable.fromIterable(chapter.second.values).flatMapSingle {
                //map each verse to a chunk and insert
                val vs = Chunk(
                        it.number,
                        "verse",
                        it.number,
                        it.number,
                        null
                )
                chunkRepository.insertForCollection(vs, chapterCollection).doOnError { println(it) }
            }
        }.toList().toCompletable()
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