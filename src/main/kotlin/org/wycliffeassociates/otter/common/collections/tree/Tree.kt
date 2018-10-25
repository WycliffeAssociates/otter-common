package org.wycliffeassociates.otter.common.collections.tree

class Tree(value: Any) : TreeNode(value) {
    val children = arrayListOf<TreeNode>()

    fun addChild(node: TreeNode) {
        children.add(node)
    }

    fun addAll(nodes: Array<TreeNode>) {
        children.addAll(nodes)
    }

    fun addAll(nodes: Collection<TreeNode>) {
        children.addAll(nodes)
    }
}

open class TreeNode(value: Any) {
    val value = value
}