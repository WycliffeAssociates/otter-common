package org.wycliffeassociates.otter.common.domain.resourcecontainer

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Project
import java.io.File
import java.io.IOException

class ImportResourceContainer(
        private val collectionRepository: ICollectionRepository,
        directoryProvider: IDirectoryProvider
) {
    enum class Result {
        SUCCESS,
        INVALID_RC,
        INVALID_CONTENT,
        UNSUPPORTED_CONTENT,
        IMPORT_ERROR,
        LOAD_RC_ERROR,
        UNKNOWN_ERROR
    }

    private val rcDirectory = File(directoryProvider.getAppDataDirectory(), "rc")

    fun import(file: File): Single<Result> {
        return when {
            file.isDirectory -> importContainerDirectory(file)
            else -> Single.just(Result.INVALID_RC)
        }
    }

    private fun importContainerDirectory(directory: File) =
        Single
                .just(directory)
                .flatMap { containerDir ->
                    // Is this a valid resource container
                    if (!validateResourceContainer(containerDir)) return@flatMap Single.just(Result.INVALID_RC)

                    // Copy to the internal directory
                    val newDirectory = copyToInternalDirectory(containerDir)

                    // Load
                    val container = try {
                         ResourceContainer.load(newDirectory, OtterResourceContainerConfig())
                    } catch (e: Exception) {
                        return@flatMap cleanUp(newDirectory, Result.LOAD_RC_ERROR)
                    }

                    val (constructResult, tree) = constructContainerTree(container)
                    if (constructResult != Result.SUCCESS) return@flatMap cleanUp(newDirectory, constructResult)

                    return@flatMap collectionRepository
                            .importResourceContainer(container, tree, container.manifest.dublinCore.language.identifier)
                            .toSingle { Result.SUCCESS }
                            .onErrorResumeNext(cleanUp(newDirectory, Result.IMPORT_ERROR))
                }
                .onErrorReturn { Result.UNKNOWN_ERROR }
                .subscribeOn(Schedulers.io())

    private fun cleanUp(containerDir: File, result: Result): Single<Result> = Single.fromCallable {
        containerDir.deleteRecursively()
        return@fromCallable result
    }

    private fun validateResourceContainer(dir: File): Boolean = dir.listFiles().map { it.name }.contains("manifest.yaml")

    private fun copyToInternalDirectory(dir: File): File {
        // Copy the resource container into the correct directory
        val destinationDirectory = rcDirectory.resolve(dir.name).absoluteFile
        if (dir.absoluteFile != destinationDirectory) {
            // Need to copy the resource container into the internal directory
            val success = dir.copyRecursively(destinationDirectory, true)
            if (!success) {
                throw IOException("Could not copy resource container ${dir.name} to resource container directory")
            }
        }
        return destinationDirectory
    }

    private fun makeExpandedContainer(container: ResourceContainer): Result {
        val dublinCore = container.manifest.dublinCore
        if (dublinCore.type == "bundle" && dublinCore.format.startsWith("text/usfm")) {
            return if (container.expandUSFMBundle()) Result.SUCCESS else Result.INVALID_CONTENT
        }
        return Result.SUCCESS
    }

    private fun constructContainerTree(container: ResourceContainer): Pair<Result, Tree> {
        val root = Tree(container.toCollection())
        val categoryInfo = container.otterConfigCategories()
        for (project in container.manifest.projects) {
            var parent = root
            for (categorySlug in project.categories) {
                // use the `latest` RC spec to treat categories as hierarchical
                // look for a matching category under the parent
                val existingCategory = parent.children
                        .map { it as? Tree }
                        .filter { (it?.value as? Collection)?.slug == categorySlug }
                        .firstOrNull()
                parent = if (existingCategory != null) {
                    existingCategory
                } else {
                    // category node does not yet exist
                    val category = categoryInfo.filter { it.identifier == categorySlug }.firstOrNull() ?: continue
                    val categoryNode = Tree(category.toCollection())
                    parent.addChild(categoryNode)
                    categoryNode
                }
            }
            val projectResult = constructProjectTree(container.dir, project)
            if (projectResult.first == Result.SUCCESS)
                parent.addChild(projectResult.second)
            else
                return Pair(projectResult.first, Tree(Unit))
        }
        return Pair(Result.SUCCESS, root)
    }

    private fun constructProjectTree(containerDir: File, project: Project): Pair<Result, Tree> {
        var result = Result.SUCCESS
        val projectLocation = containerDir.resolve(project.path)
        val projectTree = Tree(project.toCollection())
        if (projectLocation.isDirectory) {
            val files = projectLocation.listFiles()
            for (file in files) {
                result = parseFileIntoProjectTree(file, projectTree, project.identifier)
                if (result != Result.SUCCESS) return Pair(result, Tree(Unit))
            }
        } else {
            // Single file
            result = parseFileIntoProjectTree(projectLocation, projectTree, project.identifier)
            if (result != Result.SUCCESS) return Pair(result, Tree(Unit))
        }
        return Pair(result, projectTree)
    }

    private fun parseFileIntoProjectTree(file: File, root: Tree, projectIdentifier: String): Result {
        return when (file.extension) {
            "usfm", "USFM" -> {
                try {
                    val chapters = file.parseUSFMToChapterTrees(projectIdentifier)
                    root.addAll(chapters)
                    Result.SUCCESS
                } catch (e: RuntimeException) {
                    Result.INVALID_CONTENT
                }
            }
            else -> { Result.UNSUPPORTED_CONTENT }
        }
    }
}