package org.wycliffeassociates.otter.common.domain

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.domain.collections.CreateProject
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ILanguageRepository
import org.wycliffeassociates.otter.common.persistence.repositories.IResourceMetadataRepository

class ProjectCreationWizard(
        private val languageRepo: ILanguageRepository,
        private val collectionRepo: ICollectionRepository,
        private val metadataRepository: IResourceMetadataRepository,
        private val creationUseCase: CreateProject = CreateProject(collectionRepo)
) {

    val languages: Single<List<Language>> = languageRepo.getAll().cache()
    val sourceLanguages: Single<List<Language>> = metadataRepository.getAll().map {
        it.map {
            it.language
        }.distinctBy {
            it.id
        }
    }.cache()
    val projects = collectionRepo.getRootProjects().cache()



    private val collectionHierarchy: ArrayList<List<Collection>> = ArrayList()

    init {

    }

    private fun onFilterLanguages(languageList: Single<List<Language>>, query: String): Single<List<Language>> {
        return languageList.map {
            it.filter {
                it.name.contains(query, true)
                        || it.anglicizedName.contains(query, true)
                        || it.slug.contains(query, true)
            }
        }.map {
            it.sortedWith(Comparator { lang1, lang2 ->
                when {
                    lang1.slug.startsWith(query, true) -> -1
                    lang2.slug.startsWith(query, true) -> 1
                    lang1.name.startsWith(query, true) -> -1
                    lang2.name.startsWith(query, true) -> 1
                    lang1.anglicizedName.startsWith(query, true) -> -1
                    lang2.anglicizedName.startsWith(query, true) -> 1
                    else -> 0
                }
            })
        }
    }

    fun languagesValid(source: Language?, target: Language?) = source != null && target != null

    fun doOnUserSelection(selectedCollection: Collection) {
        if (selectedCollection.labelKey == "project") {
            createProject(selectedCollection)
        } else {
            showSubcollections(selectedCollection)
        }
    }

    private fun showSubcollections(collection: Collection):  {
        collectionRepo
                .getChildren(collection)
                .doOnSuccess { subcollections ->
                    collectionHierarchy.add(subcollections)
                    collections.setAll(collectionHierarchy.last().sortedBy { it.sort })
                }
    }

}

    private val projects = ArrayList<Collection>()

    val collections: ArrayList<Collection> = ArrayList()


    private val existingProjects: ObservableList<Collection> = FXCollections.observableArrayList()







    private fun createProject(selectedCollection: Collection) {
        targetLanguageProperty.value?.let { language ->
            showOverlayProperty.value = true
            creationUseCase
                    .create(selectedCollection, language)
                    .subscribe {
                        tornadofx.find(ProjectHomeViewModel::class).loadProjects()
                        showOverlayProperty.value = false
                        creationCompletedProperty.value = true
                    }
        }
    }

    fun goBack(projectWizard: Wizard) {
        when {
            collectionHierarchy.size > 1 -> {
                collectionHierarchy.removeAt(collectionHierarchy.lastIndex)
                collections.setAll(collectionHierarchy.last().sortedBy { it.sort })
            }
            collectionHierarchy.size == 1 -> {
                collectionHierarchy.removeAt(0)
                projectWizard.back()
            }
            else -> projectWizard.back()
        }
    }

    fun doesProjectExist(project: Collection): Boolean {
        return existingProjects.map { it.titleKey }.contains(project.titleKey)
    }

    fun reset() {
        clearLanguages.onNext(true)
        collections.setAll()
        collectionHierarchy.clear()
        existingProjects.clear()
        creationCompletedProperty.value = false
        loadProjects()
    }


}
