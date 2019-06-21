package org.wycliffeassociates.otter.common.domain.content

import org.wycliffeassociates.otter.common.data.model.ContentType
import java.lang.IllegalStateException

class FileNamer(
    val start: Int? = null,
    val end: Int? = null,
    val sort: Int? = null,
    val contentType: ContentType? = null,
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
        builder.contentType,
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
            sort?.let { "s$it" },
            contentType?.nullIfUnused(),
            "t$takeNumber"
        ).joinToString("_", postfix = ".wav")
    }

    private fun ContentType.nullIfUnused(): ContentType? = when (this) {
        ContentType.TEXT -> null
        else -> this
    }

    internal fun formatVerseNumber(): String? {
        val verseFormat = if (chunkCount > 99) "%03d" else "%02d"
        return when (start) {
            null -> null
            end -> verseFormat.format(start)
            else -> "$verseFormat-$verseFormat".format(start, end)
        }
    }

    internal fun formatChapterNumber(): String {
        val chapterFormat = if (chapterCount > 99) "%03d" else "%02d"
        return chapterFormat.format(chapterTitle.toIntOrNull() ?: chapterSort)
    }

    object Builder {
        var start: Int? = null
        var end: Int? = null
        var sort: Int? = null
        var contentType: ContentType? = null
        var languageSlug: String? = null
        var bookSlug: String? = null
        var rcSlug: String? = null
        var chunkCount: Long? = null
        var chapterCount: Long? = null
        var chapterTitle: String? = null
        var chapterSort: Int? = null

        fun build(): FileNamer {
            checkResourceContentTypeRequirements()
            checkMetaContentTypeRequirements()
            checkStartLessThanEnd()

            return FileNamer(this) // could throw NullPointerException if any non-nullable fields are null
        }

        private fun checkResourceContentTypeRequirements() {
            val isContentOrBody = listOf(ContentType.TITLE, ContentType.BODY).contains(contentType)
            if (isContentOrBody && listOf(sort, start, end).any { it == null }) {
                throw IllegalStateException(
                    "sort, start and end should not be null when contentType is ${contentType.toString()}.\n" +
                            "Found sort=$sort, start=$start, end=$end"
                )
            }
        }

        private fun checkMetaContentTypeRequirements() {
            // TODO: Do we need checks for ContentType.TEXT?
            // TODO: Is this requirement true?
            if (contentType == ContentType.META) {
                if (sort == null)
                    throw IllegalStateException(
                        "sort should be non-null when contentType is ${contentType.toString()}"
                    )
                if (start != null || end != null)
                    throw IllegalStateException(
                        "start and end should be null when contentType is ${contentType.toString()}"
                    )
            }
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