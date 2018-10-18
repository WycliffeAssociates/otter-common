package org.wycliffeassociates.otter.common.domain

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.*
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.common.persistence.repositories.*
import java.io.File
import java.time.LocalDate

class CreateProject(
        val languageRepo: ILanguageRepository,
        val sourceRepo: ISourceRepository,
        val collectionRepo: ICollectionRepository,
        val projectRepo: IProjectRepository,
        val chunkRepo: IChunkRepository,
        val metadataRepository: IResourceMetadataRepository,
        val directoryProvider: IDirectoryProvider
) {
    fun getAllLanguages(): Single<List<Language>> {
        return languageRepo.getAll()
    }

    fun getSourceRepos(): Single<List<Collection>> {
        return sourceRepo.getAllRoot()
    }

    fun getAll(): Single<List<Collection>> {
        return collectionRepo.getAll()
    }

    private fun insertProjectCollection(
            projCollection: Collection,
            source: Collection,
            parent: Collection? = null
    ): Single<Collection> {
        return projectRepo
                // Insert the project collection
                .insert(projCollection)
                .doOnSuccess {
                    projCollection.id = it
                }
                // Update the source
                .toCompletable()
                .andThen(projectRepo.updateSource(projCollection, source))
                // Update the parent
                .andThen(
                    if (parent != null) {
                        projectRepo.updateParent(projCollection, parent)
                    } else
                        Completable.complete()
                )
                .toSingle {
                    projCollection
                }
    }

    private fun createProjectResourceMetadata(
            sourceMetadata: ResourceMetadata,
            targetLanguage: Language
    ): ResourceMetadata {
        // Does not actually create RC on disk
        val derivedMetadata = ResourceMetadata(
                sourceMetadata.conformsTo,
                "user",
                "",
                "",
                sourceMetadata.identifier,
                LocalDate.now(),
                targetLanguage,
                LocalDate.now(),
                "",
                sourceMetadata.subject,
                "book",
                sourceMetadata.title,
                "0.0.1",
                directoryProvider.resourceContainerDirectory // TODO: Use valid path
        )

        return derivedMetadata
    }

    fun newProject(sourceProject: Collection, targetLanguage: Language): Completable {
        // Some concat maps can be removed when dao synchronization is added
        if (sourceProject.resourceContainer == null) throw NullPointerException("Source project has no metadata")

        val metadata = createProjectResourceMetadata(sourceProject.resourceContainer!!, targetLanguage)

        return metadataRepository
                // Insert and link the new metadata
                .insert(metadata)
                .map {
                    metadata.id = it
                    return@map metadata
                }
                .flatMap {
                    metadataRepository.addLink(it, sourceProject.resourceContainer!!).toSingle { it }
                }
                // Duplicate the collection structure and content
                .flatMapCompletable {
                    collectionRepo
                            .duplicateCollectionAndContent(sourceProject, it)
                            .toCompletable()
                }
    }

    fun getResourceChildren(identifier: SourceCollection): Single<List<Collection>> {
        return sourceRepo.getChildren(identifier)
    }
}