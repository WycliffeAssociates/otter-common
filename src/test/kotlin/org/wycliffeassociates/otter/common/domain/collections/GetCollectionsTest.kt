package org.wycliffeassociates.otter.common.domain.collections

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository

class GetCollectionsTest {
    private val mockCollectionRepository: ICollectionRepository = mock {
        on { getRootProjects() } doReturn Single.just(listOf(mock()))
        on { getRootSources() } doReturn  Single.just(listOf(mock()))
        on { getChildren(any()) } doReturn Single.just(listOf(mock()))
    }

    // UUT
    private val getCollections = GetCollections(mockCollectionRepository)

    @Test
    fun shouldGetRootProjectsFromRepository() {
        getCollections.rootProjects().test()
        verify(mockCollectionRepository).getRootProjects()
        verifyNoMoreInteractions(mockCollectionRepository)
    }

    @Test
    fun shouldGetRootSourcesFromRepository() {
        getCollections.rootSources().test()
        verify(mockCollectionRepository).getRootSources()
        verifyNoMoreInteractions(mockCollectionRepository)
    }

    @Test
    fun shouldGetSubcollectionsFromRepository() {
        val mockCollection: Collection = mock()
        getCollections.subcollectionsOf(mockCollection).test()
        verify(mockCollectionRepository).getChildren(mockCollection)
        verifyNoMoreInteractions(mockCollectionRepository)
    }
}