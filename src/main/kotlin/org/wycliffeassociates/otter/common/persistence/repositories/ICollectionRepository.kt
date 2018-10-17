package org.wycliffeassociates.otter.common.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.RelatedCollectionContent
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.usfm.UsfmDocument

interface ICollectionRepository : IRepository<Collection> {
    fun insert(collection: Collection): Single<Int>
    fun getBySlugAndContainer(slug: String, container: ResourceMetadata): Maybe<Collection>
    fun getChildren(collection: Collection): Single<List<Collection>>
    fun updateSource(collection: Collection, newSource: Collection): Completable
    fun updateParent(collection: Collection, newParent: Collection): Completable
    fun duplicateCollectionAndContent(obj: Collection, newMetadata: ResourceMetadata): Single<Collection>
    fun insertRelatedCollectionContent(related: RelatedCollectionContent): Completable
}