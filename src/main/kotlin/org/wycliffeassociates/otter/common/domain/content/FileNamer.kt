package org.wycliffeassociates.otter.common.domain.content

import org.wycliffeassociates.otter.common.data.model.ContentType
import kotlin.IllegalStateException

class FileNamer private constructor(
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
    private constructor(builder: Builder): this(
        builder.start,
        builder.end,
        builder.sort,
        builder.contentType!!,
        builder.languageSlug!!,
        builder.bookSlug!!,
        builder.rcSlug!!,
        builder.chunkCount!!,
        builder.chapterCount!!,
        builder.chapterTitle!!,
        builder.chapterSort!!
    )

    fun generateName(takeNumber: Int?): String {
        return listOfNotNull(
            languageSlug,
            rcSlug,
            bookSlug,
            "c${formatChapterNumber()}",
            formatVerseNumber()?.let { "v$it" },
            sort()?.let { "s$it" },
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

        return when (contentType) {
            ContentType.META -> null
            else -> when(start) {
                null -> null
                end -> verseFormat.format(start)
                else -> "$verseFormat-$verseFormat".format(start, end)
            }
        }
    }

    private fun sort(): Int? {
        return when (contentType) {
            ContentType.TITLE, ContentType.BODY -> sort
            else -> null
        }
    }

    private fun contentType(): ContentType? {
        return when (contentType) {
            ContentType.TEXT -> null
            else -> contentType
        }
    }

    abstract class Builder {
        var start: Int? = null
            protected set
        var end: Int? = null
            protected set
        var sort: Int? = null
            protected set
        var contentType: ContentType? = null
            protected set
        var languageSlug: String? = null
            protected set
        var bookSlug: String? = null
            protected set
        var rcSlug: String? = null
            protected set
        var chunkCount: Long? = null
            protected set
        var chapterCount: Long? = null
            protected set
        var chapterTitle: String? = null
            protected set
        var chapterSort: Int? = null
            protected set

        open fun build(): FileNamer {
            checkStartLessThanEnd()
            return FileNamer(this) // could throw NullPointerException if any non-nullable fields are null
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
    }
}