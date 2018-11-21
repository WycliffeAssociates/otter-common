package org.wycliffeassociates.otter.common.collections.tree

import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test

class TreeTest {
    // UUT
    private val tree = Tree(mock())

    @Test
    fun shouldAddChild() {
        val child: TreeNode = mock()
        tree.addChild(child)
        Assert.assertEquals(listOf(child), tree.children)
    }

    @Test
    fun shouldAddAllArray() {
        val children: Array<TreeNode> = arrayOf(mock(), mock())
        tree.addAll(children)
        Assert.assertEquals(children.toList(), tree.children)
    }

    @Test
    fun shouldAddAllCollection() {
        val children: Collection<TreeNode> = listOf(mock(), mock())
        tree.addAll(children)
        Assert.assertEquals(children.toList(), tree.children)
    }
}