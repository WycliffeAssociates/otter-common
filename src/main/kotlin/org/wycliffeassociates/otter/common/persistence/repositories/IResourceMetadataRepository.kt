package org.wycliffeassociates.otter.common.persistence.repositories

import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata

interface IResourceMetadataRepository : IRepository<ResourceMetadata> {
    fun insert(metadata: ResourceMetadata): Single<Int>
}