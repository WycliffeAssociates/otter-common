package org.wycliffeassociates.otter.jvm.usecases

import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.SourceCollection
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ILanguageRepository
import org.wycliffeassociates.otter.common.persistence.repositories.IProjectRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ISourceRepository

class CreateProjectUseCase(val languageRepo: ILanguageRepository,
                           val sourceRepo: ISourceRepository,
                           val collectionRepo: ICollectionRepository, val projectRepo: IProjectRepository) {

    fun getAllLanguages(): Single<List<Language>> {
        return languageRepo.getAll()
    }

    fun getSourceRepos(): Single<List<Collection>> {
        return sourceRepo.getAllRoot()
    }

    fun getAll(): Single<List<Collection>> {
        return collectionRepo.getAll()
    }

    fun newProject(collection: Collection): Single<Int> {
        return projectRepo.insert(collection)
    }

    fun getResourceChildren(identifier: SourceCollection): Single<List<Collection>> {
        return sourceRepo.getChildren(identifier)
    }

    fun updateSource(projectId: Int, sourceCollection: Collection) {
        projectRepo.getAll().doOnSuccess {
            for (projectCollection in it) {
                if (projectCollection.id == projectId) {
                    projectRepo.updateSource(projectCollection, sourceCollection)
                            .subscribe()
                }
            }
        }.subscribe()
    }
}