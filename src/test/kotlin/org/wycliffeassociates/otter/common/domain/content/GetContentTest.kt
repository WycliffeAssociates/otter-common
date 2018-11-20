package org.wycliffeassociates.otter.common.domain.content

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.repositories.IChunkRepository

class GetContentTest {
    private val mockCollection: Collection = mock()

    private val mockChunkRepository: IChunkRepository = mock {
        on { getByCollection(mockCollection) } doReturn Single.just(listOf())
    }

    // unit under test
    private val getContent = GetContent(mockChunkRepository)

    @Test
    fun shouldGetChunksFromRepository() {
        getContent.getChunks(mockCollection).test()
        verify(mockChunkRepository).getByCollection(mockCollection)
        verifyNoMoreInteractions(mockChunkRepository)
    }
}