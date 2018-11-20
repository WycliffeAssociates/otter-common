package org.wycliffeassociates.otter.common.domain.content

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.common.persistence.repositories.IChunkRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ITakeRepository

class AccessTakesTest {
    private val mockChunk: Chunk = mock()
    private val mockTake: Take = mock()
    private val take = Take("", mock(), 0, mock(), false, listOf())
    private val chunk = Chunk(0, "", 0, 0, null)

    private val mockTakeRepository: ITakeRepository = mock {
        on { getByChunk(mockChunk) } doReturn Single.just(listOf(mock(), mock()))
        on { update(take) } doReturn Completable.complete()
        on { delete(mockTake) } doReturn Completable.complete()
    }
    private val mockChunkRepository: IChunkRepository = mock {
        on { update(chunk) } doReturn Completable.complete()
    }

    // Unit under test
    private val accessTakes = AccessTakes(mockChunkRepository, mockTakeRepository)

    @Test
    fun shouldGetTakesByChunkFromRepository() {
        accessTakes.getByChunk(mockChunk).test()
        verify(mockTakeRepository).getByChunk(mockChunk)
        verifyNoMoreInteractions(mockTakeRepository)
        verifyZeroInteractions(mockChunkRepository)
    }

    @Test
    fun shouldSetSelectedTakeAndUpdateRepository() {
        accessTakes.setSelectedTake(chunk, mockTake).test()
        verify(mockChunkRepository).update(chunk)
        verifyNoMoreInteractions(mockChunkRepository)
        Assert.assertEquals(mockTake, chunk.selectedTake)
    }

    @Test
    fun shouldRemoveSelectedTakeAndUpdateRepository() {
        chunk.selectedTake = mockTake
        accessTakes.setSelectedTake(chunk, null).test()
        verify(mockChunkRepository).update(chunk)
        verifyNoMoreInteractions(mockChunkRepository)
        Assert.assertEquals(null, chunk.selectedTake)
    }

    @Test
    fun shouldSetTakePlayedAndUpdateRepository() {
        take.played = false
        accessTakes.setTakePlayed(take, true).test()
        verify(mockTakeRepository).update(take)
        verifyNoMoreInteractions(mockTakeRepository)
        verifyZeroInteractions(mockChunkRepository)
        Assert.assertEquals(true, take.played)
    }

    @Test
    fun shouldSetTakeNotPlayedAndUpdateRepository() {
        take.played = true
        accessTakes.setTakePlayed(take, false).test()
        verify(mockTakeRepository).update(take)
        verifyNoMoreInteractions(mockTakeRepository)
        verifyZeroInteractions(mockChunkRepository)
        Assert.assertEquals(false, take.played)

    }

    @Test
    fun shouldDeleteTakeFromRepository() {
        accessTakes.delete(mockTake).test()
        verify(mockTakeRepository).delete(mockTake)
        verifyNoMoreInteractions(mockTakeRepository)
        verifyZeroInteractions(mockChunkRepository)
    }

    @Test
    fun shouldGetTakeCount() {
        val testSubscriber = accessTakes.getTakeCount(mockChunk).test()
        testSubscriber.assertValue(2)
        verify(mockTakeRepository).getByChunk(mockChunk)
        verifyNoMoreInteractions(mockTakeRepository)
        verifyZeroInteractions(mockChunkRepository)
    }
}