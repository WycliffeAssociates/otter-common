package org.wycliffeassociates.otter.common.domain.content

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.common.domain.plugins.LaunchPlugin
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.common.persistence.IWaveFileCreator
import org.wycliffeassociates.otter.common.persistence.repositories.IChunkRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ITakeRepository
import java.io.File

class RecordTakesTest {
    var maxTakeNumber = 0
    var chapterCount = 0
    var verseCount = 0
    private val resourceMetadata: ResourceMetadata = mock {
        on { identifier } doReturn "rc-identifier"
        on { language } doReturn mock()
        on { language.slug } doReturn "lang-slug"
    }
    private val project: Collection = Collection(
            0,
            "book-slug",
            "book-label",
            "book-title",
            resourceMetadata
    )
    private val chapter = Collection(0, "chap-slug", "chap-label", "2", resourceMetadata)
    private val chunk = Chunk(0, "chk-label", 0, 0, mock())

    private val mockCollectionRepository: ICollectionRepository = mock()

    private val mockChunkRepository: IChunkRepository = mock()

    private val mockDirectoryProvider: IDirectoryProvider = mock {
        on { getProjectAudioDirectory(eq(project), any()) } doReturn File(".")
    }

    private val mockTakeRepository: ITakeRepository = mock {
        on { insertForChunk(any(), eq(chunk)) } doReturn Single.just(1)
    }

    private val mockWaveFileCreator: IWaveFileCreator = mock()

    private val mockLaunchPlugin: LaunchPlugin = mock {
        on { launchRecorder(any()) } doReturn Completable.complete()
    }

    // unit under test
    private val recordTake = RecordTake(
            mockCollectionRepository,
            mockChunkRepository,
            mockTakeRepository,
            mockDirectoryProvider,
            mockWaveFileCreator,
            mockLaunchPlugin
    )

    @Test
    fun shouldCreateTakeFileAndLaunchRecorder() {
        project.sort = 1
        chapter.sort = 2
        chunk.start = 3
        chunk.end = 3
        maxTakeNumber = 0
        chapterCount = 10
        verseCount = 10
        val expectedPath = "./lang-slug_rc-identifier_b01_book-slug_c02_v03_t01.wav"

        setupRepositoryMocks()

        recordTake.record(project, chapter, chunk).test()

        verifyFilepath(expectedPath)
        verifyRepositoriesAndLaunch()
    }

    @Test
    fun shouldHandleMultiVerseChunk() {
        project.sort = 1
        chapter.sort = 2
        chunk.start = 3
        chunk.end = 5
        maxTakeNumber = 0
        chapterCount = 10
        verseCount = 10
        val expectedPath = "./lang-slug_rc-identifier_b01_book-slug_c02_v03-05_t01.wav"

        setupRepositoryMocks()

        recordTake.record(project, chapter, chunk).test()

        verifyFilepath(expectedPath)
        verifyRepositoriesAndLaunch()
    }

    @Test
    fun shouldAddLeadingZeros() {
        project.sort = 1
        project.resourceContainer = resourceMetadata
        chapter.sort = 2
        chunk.start = 3
        chunk.end = 3
        maxTakeNumber = 0
        chapterCount = 120
        verseCount = 110
        val expectedPath = "./lang-slug_rc-identifier_b01_book-slug_c002_v003_t01.wav"

        setupRepositoryMocks()

        recordTake.record(project, chapter, chunk).test()

        verifyFilepath(expectedPath)
        verifyRepositoriesAndLaunch()
    }

    @Test
    fun shouldHandleNullMetadata() {
        project.sort = 1
        project.resourceContainer = null
        chapter.sort = 2
        chunk.start = 3
        chunk.end = 3
        maxTakeNumber = 0
        chapterCount = 10
        verseCount = 10
        val expectedPath = "./__b01_book-slug_c02_v03_t01.wav"

        setupRepositoryMocks()

        recordTake.record(project, chapter, chunk).test()

        verifyFilepath(expectedPath)
        verifyRepositoriesAndLaunch()
    }

    @Test
    fun shouldIncrementSortIfBibleNewTestament() {
        project.sort = 40
        project.resourceContainer = resourceMetadata
        chapter.sort = 2
        chunk.start = 3
        chunk.end = 3
        maxTakeNumber = 0
        chapterCount = 10
        verseCount = 10
        val expectedPath = "./lang-slug_rc-identifier_b41_book-slug_c02_v03_t01.wav"

        setupRepositoryMocks()
        Mockito.`when`(resourceMetadata.subject).thenReturn("Bible")

        recordTake.record(project, chapter, chunk).test()

        verifyFilepath(expectedPath)
        verifyRepositoriesAndLaunch()
    }

    @Test
    fun shouldNotIncrementSortIfNotBible() {
        project.sort = 40
        project.resourceContainer = resourceMetadata
        chapter.sort = 2
        chunk.start = 3
        chunk.end = 3
        maxTakeNumber = 0
        chapterCount = 10
        verseCount = 10
        val expectedPath = "./lang-slug_rc-identifier_b40_book-slug_c02_v03_t01.wav"

        setupRepositoryMocks()
        Mockito.`when`(resourceMetadata.subject).thenReturn("Resource")

        recordTake.record(project, chapter, chunk).test()

        verifyFilepath(expectedPath)
        verifyRepositoriesAndLaunch()
    }

    private fun setupRepositoryMocks() {
        Mockito
                .`when`(mockCollectionRepository.getChildren(project))
                .thenReturn(Single.just(listOfChapters()))

        Mockito
                .`when`(mockChunkRepository.getByCollection(chapter))
                .thenReturn(Single.just(listOfChunks()))

        Mockito
                .`when`(mockTakeRepository.getByChunk(chunk))
                .thenReturn(Single.just(listOfTakes()))
    }

    private fun verifyRepositoriesAndLaunch() {
        verify(mockTakeRepository).getByChunk(chunk)
        verify(mockTakeRepository).insertForChunk(any(), eq(chunk))

        verify(mockCollectionRepository).getChildren(project)
        verifyNoMoreInteractions(mockCollectionRepository)

        verify(mockChunkRepository).getByCollection(chapter)
        verifyNoMoreInteractions(mockChunkRepository)

        // Check if recorder is launched
        verify(mockLaunchPlugin).launchRecorder(any())
        verifyNoMoreInteractions(mockLaunchPlugin)
    }

    private fun verifyFilepath(expectedPath: String) {
        // Check if correct take file name was created
        argumentCaptor<File> {
            verify(mockWaveFileCreator).createEmpty(capture())
            verifyNoMoreInteractions(mockWaveFileCreator)
            Assert.assertEquals(expectedPath, firstValue.path)
        }
    }

    private fun listOfChapters() = List(chapterCount) { i -> mock<Collection>() }
    private fun listOfChunks() = List(verseCount) { i -> mock<Chunk>() }
    private fun listOfTakes() = List(maxTakeNumber) { i ->
        mock<Take> { on { number } doReturn i + 1 }
    }
}