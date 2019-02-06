package org.wycliffeassociates.otter.common.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.Collection

interface IContentRepository : IRepository<Content> {
    // Insert for a collection
    fun insertForCollection(content: Content, collection: Collection): Single<Int>
    // Get all the chunks for a collection
    fun getByCollection(collection: Collection): Single<List<Content>>
    // Get the chunk in a collection by the start field
    fun getByCollectionAndStart(collection: Collection, start: Int): Maybe<Content>
    // Get sources this content is derived from
    fun getSources(content: Content): Single<List<Content>>
    // Update the sources for a content
    fun updateSources(content: Content, sourceContents: List<Content>): Completable
}