package org.wycliffeassociates.otter.common.domain.content

import io.reactivex.Single
import io.reactivex.functions.Function3
import org.wycliffeassociates.otter.common.data.model.MimeType
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.common.data.workbook.Book
import org.wycliffeassociates.otter.common.data.workbook.Chapter
import org.wycliffeassociates.otter.common.data.workbook.Workbook
import org.wycliffeassociates.otter.common.domain.plugins.LaunchPlugin
import org.wycliffeassociates.otter.common.persistence.EMPTY_WAVE_FILE_SIZE
import org.wycliffeassociates.otter.common.persistence.IWaveFileCreator
import java.io.File
import java.time.LocalDate

class RecordTake(
    private val waveFileCreator: IWaveFileCreator,
    private val launchPlugin: LaunchPlugin
) {
    enum class Result {
        SUCCESS,
        NO_RECORDER,
        NO_AUDIO
    }

    private fun getContentCount(chapter: Chapter): Single<Long> = chapter.chunks.count()

    private fun getNumberOfSubcollections(project: Book): Single<Long> = project.chapters.count()

    private fun getNewTakeNumber(recordable: Recordable): Single<Int> = recordable.audio.takes.let { relay ->
        Single.just (
            relay.getValues(arrayOfNulls<Take>(relay.values.size))
                .maxBy { it.number }
                ?.number
                ?.plus(1)
                ?: 1
        )
    }

    private fun generateFilename(
        workbook: Workbook,
        chapter: Chapter,
        recordable: Recordable,
        number: Int,
        chapterCount: Long,
        chunkCount: Long,
        rcSlug: String
    ): String {
        val languageSlug = workbook.targetLanguageSlug ?: ""
        val bookSlug = workbook.target.slug
        val chapterNumber = formatChapterNumber(chapter, chapterCount)
        val verseNumber = formatVerseNumber(recordable, chunkCount)
        val sortNumber = recordable.sort
        val contentType = recordable.contentType?.toString()?.toLowerCase()
        val takeNumber = "%02d".format(number)

        return listOfNotNull(
            languageSlug,
            rcSlug,
            bookSlug,
            "c$chapterNumber",
            verseNumber?.let { "v$it" },
            sortNumber?.let { "s$it" },
            contentType,
            "t$takeNumber"
        ).joinToString("_", postfix = ".wav")
    }

    private fun formatChapterNumber(chapter: Chapter, chapterCount: Long): String {
        val chapterFormat = if (chapterCount > 99) "%03d" else "%02d"
        return chapterFormat.format(chapter.title.toIntOrNull() ?: chapter.sort)
    }

    private fun formatVerseNumber(recordable: Recordable, chunkCount: Long): String? {
        val verseFormat = if (chunkCount > 99) "%03d" else "%02d"
        return when (recordable.start) {
            null -> null
            recordable.end -> verseFormat.format(recordable.start)
            else -> "$verseFormat-$verseFormat".format(recordable.start, recordable.end)
        }
    }

    private fun create(
        workbook: Workbook,
        chapter: Chapter,
        recordable: Recordable,
        rcSlug: String,
        projectAudioDirectory: File
    ): Single<Take> = Single
        .zip(
            getNewTakeNumber(recordable),
            getNumberOfSubcollections(workbook.target),
            getContentCount(chapter),
            Function3 { newTakeNumber, chapterCount, verseCount ->
                val filename = generateFilename(
                    workbook,
                    chapter,
                    recordable,
                    newTakeNumber,
                    chapterCount,
                    verseCount,
                    rcSlug
                )
                val takeFile = getChapterAudioDirectory(projectAudioDirectory, chapter, chapterCount)
                    .resolve(File(filename))

                val newTake = Take(
                    name = takeFile.name,
                    file = takeFile,
                    number = newTakeNumber,
                    format = MimeType.WAV,
                    createdTimestamp = LocalDate.now()
                )
                waveFileCreator.createEmpty(newTake.file)
                newTake
            }
        )

    private fun getChapterAudioDirectory(projectAudioDirectory: File, chapter: Chapter, chapterCount: Long): File {
        val chapterAudioDirectory = projectAudioDirectory.resolve(formatChapterNumber(chapter, chapterCount))
        chapterAudioDirectory.mkdirs()
        return chapterAudioDirectory
    }

    fun record(
        workbook: Workbook,
        chapter: Chapter,
        recordable: Recordable,
        rcSlug: String,
        projectAudioDirectory: File
    ): Single<Result> {
        return create(workbook, chapter, recordable, rcSlug, projectAudioDirectory)
            .flatMap {
                doLaunchPlugin(recordable, it)
            }
    }

    private fun doLaunchPlugin(recordable: Recordable, take: Take): Single<Result> = launchPlugin
        .launchRecorder(take.file)
        .flatMap {
            when (it) {
                LaunchPlugin.Result.SUCCESS -> {
                    if (take.file.length() == EMPTY_WAVE_FILE_SIZE) {
                        take.file.delete()
                        Single.just(Result.NO_AUDIO)
                    } else {
                        insert(take, recordable)
                        Single.just(Result.SUCCESS)
                    }
                }
                LaunchPlugin.Result.NO_PLUGIN -> {
                    take.file.delete()
                    Single.just(Result.NO_RECORDER)
                }
            }
        }

    private fun insert(take: Take, recordable: Recordable) {
        recordable.audio.takes.accept(take)
    }
}
