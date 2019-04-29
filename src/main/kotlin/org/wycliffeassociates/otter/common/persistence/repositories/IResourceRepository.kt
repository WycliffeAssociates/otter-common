package org.wycliffeassociates.otter.common.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.workbook.HasChildBookElements
import org.wycliffeassociates.otter.common.data.workbook.ResourceContainerInfo

interface IResourceRepository : IRepository<Content> {
    fun getResources(collection: Collection, rc: ResourceContainerInfo): Observable<Content>
    fun getResources(content: Content, rc: ResourceContainerInfo): Observable<Content>
    fun getSubtreeResourceInfo(collection: Collection): List<ResourceContainerInfo>
    fun getResourceContainerInfo(content: Content): List<ResourceContainerInfo>
    fun getResourceContainerInfo(collection: Collection): List<ResourceContainerInfo>
    fun linkToContent(resource: Content, content: Content, dublinCoreFk: Int): Completable
    fun linkToCollection(resource: Content, collection: Collection, dublinCoreFk: Int): Completable

    // Prepare SubtreeHasResources table
    fun calculateAndSetSubtreeHasResources(collectionId: Int)
}