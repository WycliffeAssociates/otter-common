package org.wycliffeassociates.otter.common.domain.content

import com.jakewharton.rxrelay2.ReplayRelay
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.common.domain.plugins.LaunchPlugin
import org.wycliffeassociates.otter.common.persistence.IWaveFileCreator
import java.lang.AssertionError

class RecordTakeTest {

    private fun mockWaveFileCreator(): IWaveFileCreator = mock()
    private fun mockLaunchPlugin(): LaunchPlugin = mock()
    private fun mockTake(num: Int): Take = mock<Take>().apply {
        whenever(this.number).thenReturn(num)
    }
    private fun createTakesRelay(numTakes: Int): ReplayRelay<Take> {
        val takesRelay = ReplayRelay.create<Take>()
        for (i in 1..numTakes) {
            takesRelay.accept(mockTake(i))
        }
        return takesRelay
    }

    @Test
    fun testGetNewTakeNumber() {
        val recordTake = RecordTake(mockWaveFileCreator(), mockLaunchPlugin())

        listOf(0, 1, 2, 9, 10, 99, 100).forEach {
            val audio = AssociatedAudio(createTakesRelay(it))
            val output = recordTake.getNewTakeNumber(audio).blockingGet()
            val expected = it + 1
            try {
                Assert.assertEquals(expected, output)
            } catch (e: AssertionError) {
                println("Expected: $expected, Output: $output")
            }
        }
    }
}