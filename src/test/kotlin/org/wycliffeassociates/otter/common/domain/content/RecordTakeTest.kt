package org.wycliffeassociates.otter.common.domain.content

import com.jakewharton.rxrelay2.ReplayRelay
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.Chapter
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.common.domain.plugins.LaunchPlugin
import org.wycliffeassociates.otter.common.persistence.EMPTY_WAVE_FILE_SIZE
import java.io.File
import java.lang.AssertionError

class RecordTakeTest {
    private val recordTake = Mockito.spy(RecordTake(mock(), mock())).apply {
        Mockito.doNothing().whenever(this).insert(any(), any())
    }

    private fun mockTakeWithNum(num: Int): Take = mock<Take>().apply {
        whenever(this.number).thenReturn(num)
    }
    private fun mockTakeWithFile(): Take = mockTakeWithFileLength(EMPTY_WAVE_FILE_SIZE + 1)
    private fun mockTakeWithEmptyFile(): Take = mockTakeWithFileLength(EMPTY_WAVE_FILE_SIZE)
    private fun mockTakeWithFileLength(fileLength: Long): Take {
        val file = mock<File>().apply {
            whenever(this.length()).thenReturn(fileLength)
        }
        return mock<Take>().apply {
            whenever(this.file).thenReturn(file)
        }
    }
    private fun mockChapter(title: String, sort: Int): Chapter = mock<Chapter>().apply {
        whenever(this.title).thenReturn(title)
        whenever(this.sort).thenReturn(sort)
    }
    private fun mockRecordable(start: Int?, end: Int?): Recordable = mock<Recordable>().apply {
        whenever(this.start).thenReturn(start)
        whenever(this.end).thenReturn(end)
    }
    private fun createTakesRelay(numTakes: Int): ReplayRelay<Take> {
        val takesRelay = ReplayRelay.create<Take>()
        for (i in 1..numTakes) {
            takesRelay.accept(mockTakeWithNum(i))
        }
        return takesRelay
    }

    private fun <T> doAssertEquals(expected: T, output: T) {
        try {
            Assert.assertEquals(expected, output)
        } catch (e: AssertionError) {
            println("Expected: $expected, Output: $output")
        }
    }

    @Test
    fun testGetNewTakeNumber() {
        listOf(0, 1, 2, 9, 10, 99, 100).forEach {
            val audio = AssociatedAudio(createTakesRelay(it))
            val output = recordTake.getNewTakeNumber(audio).blockingGet()
            val expected = it + 1
            doAssertEquals(expected, output)
        }
    }

    data class FormatChapterNumberTestCase(val title: String, val sort: Int, val chapterCount: Int)
    private fun fcnCase(title: String, sort: Int, chapterCount: Int) =
        FormatChapterNumberTestCase(title, sort, chapterCount)
    @Test
    fun testFormatChapterNumber() {
        val sort = 5
        // first: Chapter.title, second: Chapter.sort, third: chapterCount
        val cases = mapOf(
            // title represents an int
            //      chapterCount < 100
            fcnCase("1", sort, 20) to "01",
            fcnCase("4", sort, 99) to "04",
            fcnCase("10", sort, 99) to "10",
            //      chapterCount >= 100
            fcnCase("1", sort, 100) to "001",
            fcnCase("10", sort, 100) to "010",
            fcnCase("1", sort, 120) to "001",
            fcnCase("10", sort, 120) to "010",
            fcnCase("100", sort, 120) to "100",
            // title does not represent an int
            //      chapterCount < 100
            fcnCase("title", 5, 20) to "05",
            fcnCase("title", 5, 99) to "05",
            fcnCase("title", 10, 99) to "10",
            //      chapterCount >= 100
            fcnCase("title", 5, 100) to "005",
            fcnCase("title", 10, 101) to "010",
            fcnCase("title", 100, 101) to "100"
        )
        cases.entries.forEach {
            val chapter = mockChapter(it.key.title, it.key.sort)
            val output = recordTake.formatChapterNumber(chapter, it.key.chapterCount.toLong())
            val expected = it.value
            doAssertEquals(expected, output)
        }
    }

    data class FormatVerseNumberTestCase(val start: Int?, val end: Int?, val chunkCount: Int)
    private fun fvnCase(start: Int?, end: Int?, chunkCount: Int) =
        FormatVerseNumberTestCase(start, end, chunkCount)
    @Test
    fun testFormatVerseNumber() {
        // first: Recordable.start, second: Recordable.end, third: chunkCount
        val cases = mapOf(
            // start == null
            fvnCase(null, null, 10) to null,
            // start == end
            //      chunkCount < 100
            fvnCase(4, 4, 10) to "04",
            fvnCase(4, 4, 99) to "04",
            //      chunkCount >= 100
            fvnCase(4, 4, 100) to "004",
            fvnCase(10, 10, 100) to "010",
            fvnCase(4, 4, 101) to "004",
            fvnCase(100, 100, 101) to "100",
            // start != end
            //      chunkCount < 100
            fvnCase(4, 5, 10) to "04-05",
            fvnCase(4, 5, 99) to "04-05",
            fvnCase(10, 20, 99) to "10-20",
            //      chunkCount >= 100
            fvnCase(4, 5, 100) to "004-005",
            fvnCase(4, 25, 120) to "004-025",
            fvnCase(10, 20, 120) to "010-020",
            fvnCase(10, 120, 120) to "010-120"
        )
        cases.entries.forEach {
            val recordable = mockRecordable(it.key.start, it.key.end)
            val output = recordTake.formatVerseNumber(recordable, it.key.chunkCount.toLong())
            val expected = it.value
            doAssertEquals(expected, output)
        }
    }

    @Test
    fun testHandleEmptyWaveFile() {
        val take = mockTakeWithEmptyFile()
        val result = recordTake.handlePluginResult(mock(), take, LaunchPlugin.Result.SUCCESS)
        doAssertEquals(RecordTake.Result.NO_AUDIO, result)
        verify(take.file, times(1)).delete()
    }

    @Test
    fun testHandleSuccess() {
        val take = mockTakeWithFile()
        val recordable = mock<Recordable>()
        val result = recordTake.handlePluginResult(recordable, take, LaunchPlugin.Result.SUCCESS)
        doAssertEquals(RecordTake.Result.SUCCESS, result)
        verify(take.file, times(0)).delete()
        verify(recordTake, times(1)).insert(take, recordable)
    }

    @Test
    fun testHandleNoPlugin() {
        val take = mockTakeWithFile()
        val result = recordTake.handlePluginResult(mock(), take, LaunchPlugin.Result.NO_PLUGIN)
        doAssertEquals(RecordTake.Result.NO_RECORDER, result)
        verify(take.file, times(1)).delete()
    }
}