package org.wycliffeassociates.otter.common.domain.content

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.repositories.IChunkRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ITakeRepository

class GetContentTest {
    private val mockCollection: Collection = mock()
    private val mockChunk: Chunk = mock()

    private val mockCollectionRepository: ICollectionRepository = mock {
        on { getChildren(mockCollection) } doReturn Single.just(listOf())
    }
    private val mockChunkRepository: IChunkRepository = mock {
        on { getByCollection(mockCollection) } doReturn Single.just(listOf())
    }
    private val mockTakeRepostiory: ITakeRepository = mock {
        on { getByChunk(mockChunk) } doReturn Single.just(listOf(mock()))
    }

    // unit under test
    private val getContent = GetContent(mockCollectionRepository, mockChunkRepository, mockTakeRepostiory)

    @Test
    fun shouldGetSubcollectionsFromRepository() {
        getContent.getSubcollections(mockCollection).test()
        verify(mockCollectionRepository).getChildren(mockCollection)
        verifyNoMoreInteractions(mockCollectionRepository)
        verifyZeroInteractions(mockChunkRepository)
        verifyZeroInteractions(mockTakeRepostiory)
    }

    @Test
    fun shouldGetChunksFromRepository() {
        getContent.getChunks(mockCollection).test()
        verify(mockChunkRepository).getByCollection(mockCollection)
        verifyNoMoreInteractions(mockChunkRepository)
        verifyZeroInteractions(mockCollectionRepository)
        verifyZeroInteractions(mockTakeRepostiory)
    }

    @Test
    fun shouldGetTakeCountFromRepository() {
        val testObserver = getContent.getTakeCount(mockChunk).test()
        verify(mockTakeRepostiory).getByChunk(mockChunk)
        verifyNoMoreInteractions(mockTakeRepostiory)
        verifyZeroInteractions(mockCollectionRepository)
        verifyZeroInteractions(mockChunkRepository)
        testObserver.assertValue(1)
    }
}