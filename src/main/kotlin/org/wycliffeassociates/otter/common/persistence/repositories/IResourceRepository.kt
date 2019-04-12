package org.wycliffeassociates.otter.common.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.Collection

interface IResourceRepository : IRepository<Content> {
    // Get resources for a content
    fun getByContent(content: Content): Single<List<Content>>
    // Get resources for a collection
    fun getByCollection(collection: Collection): Single<List<Content>>
    // Link
    fun linkToContent(resource: Content, content: Content, dublinCoreFk: Int): Completable
    fun linkToCollection(resource: Content, collection: Collection, dublinCoreFk: Int): Completable
    // Prepare SubtreeHasResources table
    fun calculateAndSetSubtreeHasResources(collectionId: Int)
}