//package org.wycliffeassociates.otter.common.domain.resourcecontainer.project
//
//import org.wycliffeassociates.otter.common.collections.tree.OtterTree
//import java.io.File
//import java.util.*
//import java.util.zip.ZipFile
//
//class ZipFileSystem {
//
//    private fun buildTree(zipFile: ZipFile) {
//
//        val list = zipFile.entries().toList()
//
//        //TODO
//        val rootCollection = Collection(0, "en_tn", "tn", "0", null)
//        //TODO: Any or either?
//        val root = OtterTree<OtterZipFile>() // TODO: Name or path?
//
////        val stack = ArrayDeque<StackNode>()
//
//        val stack = ArrayDeque<Pair<Regex, OtterTree<Any>>>()
//        stack.push(Pair(Regex(".*"), root)) // Create root node that matches everything
//
//        list.forEach { zipEntry ->
//            if (!extensions.matches(zipEntry.name)) {
//                return@forEach // continue
//            }
//            while (!stack.peek().first.matches(zipEntry.name)) {
//                stack.pop()
//            }
//            // Get the remainder of the path that did not match the node regex and split on "/"
//            val parts = stack.peek().first.split(zipEntry.name)[1].split(File.separator)
//            parts.forEach { part ->
//                if (extensions.matches(part)) {
//                    // TODO fileId
//                    stack.peek().second.addAll(
//                            contentNodeList(zipFile.bufferedReaderProvider(zipEntry), 0)
//                    )
//                } else {
//                    val tree = OtterTree<Any>(Collection(0, "test", "tn", "0", null)) // TODO
//                    stack.peek().second.addChild(tree)
//                    stack.push(Pair(Regex(".*"), tree)) // TODO regex
//                }
//            }
//        }
//    }
//}

