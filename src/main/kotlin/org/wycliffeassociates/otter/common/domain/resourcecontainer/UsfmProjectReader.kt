package org.wycliffeassociates.otter.common.domain.resourcecontainer

import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.domain.usfm.ParseUsfm
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Project
import java.io.File
import java.io.IOException

class UsfmProjectReader: IProjectFileReader {
    override fun constructProjectTree(
            container: ResourceContainer, project: Project
    ): Pair<ImportResourceContainer.Result, Tree> {
        var result = ImportResourceContainer.Result.SUCCESS
        val projectLocation = container.dir.resolve(project.path)
        val projectTree = Tree(project.toCollection())
        if (projectLocation.isDirectory) {
            val files = projectLocation.listFiles()
            for (file in files) {
                result = parseFileIntoProjectTree(file, projectTree, project.identifier)
                if (result != ImportResourceContainer.Result.SUCCESS) return Pair(result, Tree(Unit))
            }
        } else {
            // Single file
            result = parseFileIntoProjectTree(projectLocation, projectTree, project.identifier)
            if (result != ImportResourceContainer.Result.SUCCESS) return Pair(result, Tree(Unit))
        }
        return Pair(result, projectTree)
    }

    private fun parseFileIntoProjectTree(file: File, root: Tree, projectIdentifier: String): ImportResourceContainer.Result {
        return when (file.extension) {
            "usfm", "USFM" -> {
                try {
                    val chapters = parseUSFMToChapterTrees(file, projectIdentifier)
                    root.addAll(chapters)
                    ImportResourceContainer.Result.SUCCESS
                } catch (e: RuntimeException) {
                    ImportResourceContainer.Result.INVALID_CONTENT
                }
            }
            else -> { ImportResourceContainer.Result.UNSUPPORTED_CONTENT }
        }
    }

    private fun parseUSFMToChapterTrees(usfmFile: File, projectSlug: String): List<Tree> {
        if (usfmFile.extension != "usfm") {
            throw IOException("Not a USFM file")
        }

        val doc = ParseUsfm(usfmFile).parse()
        return doc.chapters.map { chapter ->
            val chapterSlug = "${projectSlug}_${chapter.key}"
            val chapterCollection = Collection(
                    chapter.key,
                    chapterSlug,
                    "chapter",
                    chapter.key.toString(),
                    null
            )
            val chapterTree = Tree(chapterCollection)
            // create a chunk for the whole chapter
            val chapChunk = Content(
                    0,
                    "chapter",
                    chapter.value.values.first().number,
                    chapter.value.values.last().number,
                    null
            )
            chapterTree.addChild(TreeNode(chapChunk))

            // Create content for each verse
            for (verse in chapter.value.values) {
                val content = Content(verse.number, "verse", verse.number, verse.number, null)
                chapterTree.addChild(TreeNode(content))
            }
            return@map chapterTree
        }
    }
}