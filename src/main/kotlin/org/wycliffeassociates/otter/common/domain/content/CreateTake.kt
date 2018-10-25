package org.wycliffeassociates.otter.common.domain.content

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.common.domain.plugins.LaunchPlugin
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.common.persistence.IWaveFileCreator
import org.wycliffeassociates.otter.common.persistence.repositories.IChunkRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ITakeRepository
import java.io.File
import java.time.LocalDate

class CreateTake(
        private val collectionRepository: ICollectionRepository,
        private val chunkRepository: IChunkRepository,
        private val takeRepository: ITakeRepository,
        private val directoryProvider: IDirectoryProvider,
        private val waveFileCreator: IWaveFileCreator,
        private val launchPlugin: LaunchPlugin
) {
    private fun getNumberOfChunks(collection: Collection): Single<Int> = chunkRepository
            .getByCollection(collection)
            .map { it.size }

    private fun getNumberOfSubcollections(collection: Collection): Single<Int> = collectionRepository
            .getChildren(collection)
            .map { it.size }

    private fun getHighestTakeNumber(chunk: Chunk): Single<Int> = takeRepository
            .getByChunk(chunk)
            .map { takes ->
                takes.maxBy { it.number }?.number ?: 0
            }

    private fun generateTakeFilename(
            project: Collection,
            chapter: Collection,
            chunk: Chunk,
            number: Int,
            chapterCount: Int,
            chunkCount: Int
    ): String {
        // Get the correct format specifiers
        val chapterFormat = if (chapterCount > 99) "%03d" else "%02d"
        val verseFormat = if (chunkCount > 99) "%03d" else "%02d"

        // Format each piece of the filename
        val languageSlug = project.resourceContainer?.language?.slug ?: ""
        val rcSlug = project.resourceContainer?.identifier ?: ""
        val bookNumber = "%02d".format(
                // Handle book number offset (only for Bibles)
                if (project.resourceContainer?.subject?.toLowerCase() == "bible"
                        && project.sort > 39) project.sort + 1 else project.sort
        )
        val bookSlug = project.slug
        val chapterNumber = chapterFormat.format(chapter.titleKey.toInt())
        val verseNumber = if (chunk.start == chunk.end) {
            verseFormat.format(chunk.start)
        } else {
            "$verseFormat-$verseFormat".format(chunk.start, chunk.end)
        }
        val takeNumber = "%02d".format(number)

        // Compile the complete filename
        return listOf(
                languageSlug,
                rcSlug,
                "b$bookNumber",
                bookSlug,
                "c$chapterNumber",
                "v$verseNumber",
                "t$takeNumber"
        ).joinToString("_", postfix = ".wav")
    }

    fun create(chunk: Chunk, project: Collection, chapter: Collection): Single<Take> = Single
            .zip(
                    getHighestTakeNumber(chunk),
                    getNumberOfSubcollections(project),
                    getNumberOfChunks(chapter),
                    Function3 { highest, chapterCount, verseCount ->
                        val filename = generateTakeFilename(
                                project,
                                chapter,
                                chunk,
                                highest + 1,
                                chapterCount,
                                verseCount
                        )

                        // Create a file for this take
                        val chapterFormat = if (chapterCount > 99) "%03d" else "%02d"

                        val takeFile = directoryProvider
                                .getProjectAudioDirectory(project, chapterFormat.format(chapter.titleKey.toInt()))
                                .resolve(File(filename))

                        val newTake = Take(
                                takeFile.name,
                                takeFile,
                                highest + 1,
                                LocalDate.now(),
                                false,
                                listOf() // No markers
                        )

                        // Create an empty WAV file
                        waveFileCreator.createEmpty(newTake.path)
                        return@Function3 newTake
                    }
            )

    fun saveNew(take: Take, chunk: Chunk): Completable = takeRepository
            .insertForChunk(take, chunk)
            .toCompletable()

    fun recordAndSaveNewTake(chunk: Chunk, project: Collection, chapter: Collection): Completable {
        return create(chunk, project, chapter)
                .flatMap { take ->
                    launchPlugin
                            .launchDefaultPlugin(take.path)
                            .toSingle { take }
                }
                .flatMapCompletable { take ->
                    saveNew(take, chunk)
                }
    }
}