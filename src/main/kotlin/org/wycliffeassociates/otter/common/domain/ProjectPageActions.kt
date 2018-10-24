package org.wycliffeassociates.otter.common.domain

import io.reactivex.Completable
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.audioplugin.IAudioPlugin
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.common.persistence.IWaveFileCreator
import org.wycliffeassociates.otter.common.persistence.repositories.IAudioPluginRepository
import org.wycliffeassociates.otter.common.persistence.repositories.IChunkRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.ITakeRepository
import java.io.File
import java.time.LocalDate

class ProjectPageActions(
        private val directoryProvider: IDirectoryProvider,
        private val waveFileCreator: IWaveFileCreator,
        private val collectionRepo: ICollectionRepository,
        private val chunkRepo: IChunkRepository,
        private val takeRepo: ITakeRepository,
        private val pluginRepo: IAudioPluginRepository
) {
    fun getChildren(projectRoot: Collection): Single<List<Collection>> {
        return collectionRepo.getChildren(projectRoot)
    }

    fun getChunks(collection: Collection): Single<List<Chunk>> {
        return chunkRepo.getByCollection(collection)
    }

    fun getTakeCount(chunk: Chunk): Single<Int> {
        return takeRepo
                .getByChunk(chunk)
                .map {
                    it.size
                }
    }

    fun getHighestTakeNumber(chunk: Chunk): Single<Int> {
        return takeRepo
                .getByChunk(chunk)
                .map { takes ->
                    takes.maxBy { it.number }?.number ?: 0
                }
    }

    fun insertTake(take: Take, chunk: Chunk): Single<Int> {
        return takeRepo.insertForChunk(take, chunk)
    }

    fun updateTake(take: Take): Completable {
        return takeRepo.update(take)
    }

    fun createNewTake(chunk: Chunk, project: Collection, chapter: Collection): Single<Take> {
        return getHighestTakeNumber(chunk)
                .map { highestNumber ->
                    // Create a file for this take
                    val languageSlug = project.resourceContainer?.language?.slug ?: ""
                    val rcSlug = project.resourceContainer?.identifier ?: ""
                    val bookNumber = "%02d".format(
                            // Handle book number offset
                            if (project.sort > 39) project.sort + 1 else project.sort
                    )
                    val bookSlug = project.slug
                    val chapterNumber = "%02d".format(chapter.titleKey.toInt())
                    val verseNumber = if (chunk.start == chunk.end) {
                        "%02d".format(chunk.start)
                    } else {
                        "%02d-%02d".format(chunk.start, chunk.end)
                    }
                    val takeNumber = "%02d".format(highestNumber + 1)
                    val filename = listOf(
                            languageSlug,
                            rcSlug,
                            "b$bookNumber",
                            bookSlug,
                            "c$chapterNumber",
                            "v$verseNumber",
                            "t$takeNumber"
                    ).joinToString("_", postfix = ".wav")

                    val takeFile = directoryProvider
                            .getProjectAudioDirectory(project, chapter.titleKey.toInt())
                            .resolve(File(filename))

                    val newTake = Take(
                            takeFile.name,
                            takeFile,
                            highestNumber + 1,
                            LocalDate.now(),
                            false,
                            listOf() // No markers
                    )

                    // Create an empty WAV file
                    waveFileCreator.createEmpty(newTake.path)

                    newTake
                }
    }

    fun launchDefaultPluginForTake(take: Take): Completable {
        return pluginRepo
                .getDefaultPlugin()
                .flatMapCompletable {
                    it.launch(take.path)
                }
    }
}