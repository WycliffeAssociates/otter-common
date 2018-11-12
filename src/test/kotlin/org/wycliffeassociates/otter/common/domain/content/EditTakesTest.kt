package org.wycliffeassociates.otter.common.domain.content

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.common.domain.plugins.LaunchPlugin
import org.wycliffeassociates.otter.common.persistence.repositories.ITakeRepository
import java.io.File
import java.time.LocalDate

class EditTakesTest {
    private val mockFile: File = mock()
    private val take = Take("", mockFile,0, LocalDate.now(), false, listOf())

    private val mockTakeRepository: ITakeRepository = mock {
        on { update(take) } doReturn Completable.complete()
    }
    private val mockLaunchPlugin: LaunchPlugin = mock {
        on { launchEditor(mockFile) } doReturn Completable.complete()
    }

    // unit under test
    private val editTake = EditTake(mockTakeRepository, mockLaunchPlugin)

    @Test
    fun shouldUpdateTimestampAndLaunchEditor() {
        take.timestamp = LocalDate.of(2018, 10, 19)
        editTake.edit(take).test()
        verify(mockTakeRepository).update(take)
        verifyNoMoreInteractions(mockTakeRepository)
        verify(mockLaunchPlugin).launchEditor(mockFile)
        verifyNoMoreInteractions(mockLaunchPlugin)
        Assert.assertEquals(LocalDate.now(), take.timestamp)
    }
}