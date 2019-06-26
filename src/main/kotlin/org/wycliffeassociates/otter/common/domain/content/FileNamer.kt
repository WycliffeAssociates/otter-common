package org.wycliffeassociates.otter.common.domain.content

import org.wycliffeassociates.otter.common.data.model.ContentType
import kotlin.IllegalStateException

class FileNamer(
    val start: Int? = null,
    val end: Int? = null,
    val sort: Int? = null,
    val contentType: ContentType,
    val languageSlug: String,
    val bookSlug: String,
    val rcSlug: String,
    val chunkCount: Long,
    val chapterCount: Long,
    val chapterTitle: String,
    val chapterSort: Int
) {
    init {
        checkStartLessThanEnd()
    }

    private fun checkStartLessThanEnd() {
        if (end != null && start == null)
            throw IllegalStateException("start should not be null if end is not null")
        if (start != null && end == null)
            throw IllegalStateException("end should not be null if start is not null")
        start?.let { s ->
            end?.let { e ->
                if (s > e)
                    throw IllegalStateException("start > end")
            }
        }
    }

    fun generateName(takeNumber: Int?): String {
        return listOfNotNull(
            languageSlug,
            rcSlug,
            bookSlug,
            "c${formatChapterNumber()}",
            formatVerseNumber()?.let { "v$it" },
            sort?.let { "s$it" },
            contentType(),
            "t$takeNumber"
        ).joinToString("_", postfix = ".wav")
    }

    internal fun formatChapterNumber(): String {
        val chapterFormat = if (chapterCount > 99) "%03d" else "%02d"
        return chapterFormat.format(chapterTitle.toIntOrNull() ?: chapterSort)
    }

    internal fun formatVerseNumber(): String? {
        val verseFormat = if (chunkCount > 99) "%03d" else "%02d"

        return when(start) {
            null -> null
            end -> verseFormat.format(start)
            else -> "$verseFormat-$verseFormat".format(start, end)
        }
    }

    private fun contentType(): ContentType? {
        return when (contentType) {
            ContentType.TEXT -> null
            else -> contentType
        }
    }
}