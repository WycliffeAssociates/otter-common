package org.wycliffeassociates.otter.common.domain.resourcecontainer

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.DocumentParser
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.domain.mapper.BookMapper
import org.wycliffeassociates.otter.common.domain.usfm.ParseUsfm
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.common.persistence.repositories.IContentRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ILanguageRepository
import org.wycliffeassociates.otter.common.persistence.repositories.IResourceMetadataRepository
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Manifest
import org.wycliffeassociates.resourcecontainer.entity.Project
import org.wycliffeassociates.resourcecontainer.errors.RCException
import java.io.File
import java.io.IOException


class ImportResourceContainer(
        private val collectionRepository: ICollectionRepository,
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
        return Single.fromCallable {
            val rc = ResourceContainer.load(container, OtterResourceContainerConfig())
            val dc = rc.manifest.dublinCore
//            if (dc.type == "bundle" && dc.format == "text/usfm") {
//                expandResourceContainerBundle(rc)
//            }
            val dataStructure = DocumentParser().parse(rc)
//            val tree = BookMapper().mapToTree(dataStructure, rc)
            return@fromCallable Triple(BookMapper().mapToTree(dataStructure, rc), rc, dc)
        }.flatMapCompletable { (tree, rc, dc) ->
            collectionRepository.importResourceContainer(rc, tree, dc.language.identifier)
        }.subscribeOn(Schedulers.io())
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