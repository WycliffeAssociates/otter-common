package org.wycliffeassociates.otter.common.domain.collections

import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository

class GetCollections(private val collectionRepo: ICollectionRepository) {
    fun rootSources(): Single<List<Collection>> {
        return collectionRepo.getRootSources()
    }
    fun rootProjects(): Single<List<Collection>> {
        return collectionRepo.getRootProjects()
    }
}