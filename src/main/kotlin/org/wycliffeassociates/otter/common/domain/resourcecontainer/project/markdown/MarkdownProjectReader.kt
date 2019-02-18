package org.wycliffeassociates.otter.common.domain.resourcecontainer.project.markdown

import org.wycliffeassociates.otter.common.collections.tree.OtterTree
import org.wycliffeassociates.otter.common.collections.tree.OtterTreeNode
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.domain.resourcecontainer.ImportResult
import org.wycliffeassociates.otter.common.domain.resourcecontainer.project.IProjectReader
import org.wycliffeassociates.otter.common.domain.resourcecontainer.project.OtterFile
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Project
import java.io.BufferedReader
import java.io.File
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

private const val FORMAT = "text/markdown"
private val extensions = Regex(".+\\.(md|mkdn?|mdown|markdown)$", RegexOption.IGNORE_CASE)

class MarkdownProjectReader() : IProjectReader {
    override fun constructProjectTree(container: ResourceContainer, project: Project)
            : Pair<ImportResult, Tree> {

        // TODO: Logic for projectRoot does not apply to zip
        val projectRoot = container.file.resolve(project.path)
        val collectionKey = container.manifest.dublinCore.identifier
        return projectRoot
                // TODO: Change to buildZipEntryTree
                .buildFileTree()
                .filterMarkdownFiles()
                ?.map<Any> { f -> contentList(f) ?: collection(collectionKey, f, OtterFile.F(projectRoot)) }
                ?.flattenContent()
                ?.let { Pair(ImportResult.SUCCESS, it) }
                ?: Pair(ImportResult.LOAD_RC_ERROR, Tree(Unit))
    }

    private fun fileToId(f: OtterFile): Int =
            f.nameWithoutExtension().toIntOrNull() ?: 0

    private fun fileToSlug(f: OtterFile, root: OtterFile): String =
            f.toRelativeString(root.parentFile())
                    .split('/', '\\')
                    .map { it.toIntOrNull()?.toString() ?: it }
                    .joinToString("_")

    private fun bufferedReaderProvider(f: OtterFile): (() -> BufferedReader)? =
            if (f.isFile()) {
                { f.bufferedReader() }
            } else null

    private fun collection(key: String, f: OtterFile, projectRoot: OtterFile): Collection =
            collection(key, fileToSlug(f, projectRoot), fileToId(f))

    private fun collection(key: String, slug: String, id: Int): Collection =
            Collection(
                    sort = id,
                    slug = slug,
                    labelKey = key,
                    titleKey = "$id",
                    resourceContainer = null)

    private fun content(sort: Int, label: String, id: Int, text: String): Content? =
            if (text.isEmpty()) null
            else Content(sort, label, id, id, null, text, FORMAT)

    private fun contentList(f: OtterFile): List<Content>? =
            bufferedReaderProvider(f)
                    ?.let { contentList(it, fileToId(f)) }

    private fun contentList(brp: () -> BufferedReader, fileId: Int): List<Content> {
        val helpResources = brp().use { ParseMd.parse(it) }
        var sort = 1
        return helpResources.flatMap { helpResource ->
            listOfNotNull(
                    content(sort++, "title", fileId, helpResource.title),
                    content(sort++, "body", fileId, helpResource.body)
            )
        }
    }

    private fun contentNodeList(brp: () -> BufferedReader, fileId: Int): List<OtterTreeNode<Content>> {
        val helpResources = brp().use { ParseMd.parse(it) }
        var sort = 1
        return helpResources.flatMap { helpResource ->
            listOfNotNull(
                    content(sort++, "title", fileId, helpResource.title),
                    content(sort++, "body", fileId, helpResource.body)
            ).map {
                OtterTreeNode(it)
            }
        }
    }

//    data class StackNode(val pathRegex: Regex, val node: OtterTree<Any>)

    private fun buildZeTree(zipFile: ZipFile) {

        val list = zipFile.entries().toList()

        //TODO
        val rootCollection = Collection(0, "en_tn", "tn", "0", null)
        //TODO: Any or either?
        val root = OtterTree<Any>(rootCollection)

//        val stack = ArrayDeque<StackNode>()

        val stack = ArrayDeque<Pair<Regex, OtterTree<Any>>>()
        stack.push(Pair(Regex(".*"), root)) // Create root node that matches everything

        list.forEach { zipEntry ->
            if (!extensions.matches(zipEntry.name)) {
                return@forEach // continue
            }
            while (!stack.peek().first.matches(zipEntry.name)) {
                stack.pop()
            }
            // Get the remainder of the path that did not match the node regex and split on "/"
            val parts = stack.peek().first.split(zipEntry.name)[1].split(File.separator)
            parts.forEach { part ->
                if (extensions.matches(part)) {
                    // TODO fileId
                    stack.peek().second.addAll(
                            contentNodeList(zipFile.bufferedReaderProvider(zipEntry), 0)
                    )
                } else {
                    val tree = OtterTree<Any>(Collection(0, "test", "tn", "0", null)) // TODO
                    stack.peek().second.addChild(tree)
                    stack.push(Pair(Regex(".*"), tree)) // TODO regex
                }
            }
        }
    }
}

internal fun ZipFile.bufferedReaderProvider(ze: ZipEntry): (() -> BufferedReader) = {
    this.getInputStream(ze).bufferedReader()
}

internal fun File.buildFileTree(): OtterTree<OtterFile> {
    // TODO: Anywhere there is a file, just create an OtterFile(file)
    var treeRoot: OtterTree<OtterFile>? = null
    val treeCursor = ArrayDeque<OtterTree<OtterFile>>()
    this.walkTopDown()
            .onEnter { newDir ->
                OtterTree(OtterFile.F(newDir) as OtterFile).let { newDirNode ->
                    treeCursor.peek()?.addChild(newDirNode)
                    treeCursor.push(newDirNode)
                    true
                }
            }
            .onLeave { treeRoot = treeCursor.pop() }
            .filter { it.isFile }
            .map { OtterTreeNode(OtterFile.F(it)) }
            .forEach { treeCursor.peek()?.addChild(it) }
    return treeRoot ?: OtterTree(OtterFile.F(this) as OtterFile)
}

private fun OtterTree<OtterFile>.filterMarkdownFiles(): OtterTree<OtterFile>? =
        this.filterPreserveParents { it.isFile() && extensions.matches(it.name()) }

private fun Tree.flattenContent(): Tree =
        Tree(this.value).also {
            it.addAll(
                    if (this.children.all { c -> c.value is List<*> }) {
                        this.children
                                .flatMap { c -> c.value as List<*> }
                                .filterNotNull()
                                .map { TreeNode(it) }
                    } else {
                        this.children
                                .map { if (it is Tree) it.flattenContent() else it }
                    }
            )
        }
