package org.wycliffeassociates.otter.common.domain.resourcecontainer

import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.domain.usfm.ParseUsfm
import java.io.File
import java.io.IOException

fun File.parseUSFMToChapterTrees(projectSlug: String): List<Tree> {
    if (extension != "usfm")
        throw IOException("Not a USFM file")

    val doc = ParseUsfm(this).parse()
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
        val chapChunk = Content(0, "chapter",
                chapter.value.values.first().number,
                chapter.value.values.last().number, null)
        chapterTree.addChild(TreeNode(chapChunk))

        // Create content for each verse
        for (verse in chapter.value.values) {
            val content = Content(verse.number, "verse", verse.number, verse.number, null)
            chapterTree.addChild(TreeNode(content))
        }
        return@map chapterTree
    }
}