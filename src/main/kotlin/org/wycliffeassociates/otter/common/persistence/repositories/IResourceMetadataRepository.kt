package org.wycliffeassociates.otter.common.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata

interface IResourceMetadataRepository : IRepository<ResourceMetadata> {
    fun insert(metadata: ResourceMetadata): Single<Int>
    // These functions are commutative
    fun addLink(firstMetadata: ResourceMetadata, secondMetadata: ResourceMetadata): Completable
    fun removeLink(firstMetadata: ResourceMetadata, secondMetadata: ResourceMetadata): Completable
    fun getLinked(metadata: ResourceMetadata): Single<List<ResourceMetadata>>
}