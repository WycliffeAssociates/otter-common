package org.wycliffeassociates.otter.common.domain.content

import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.data.workbook.*

object WorkbookFileNamerBuilder {
    fun createFileNamer(
        workbook: Workbook,
        chapter: Chapter,
        chunk: Chunk?,
        recordable: Recordable,
        rcSlug: String
    ) = FileNamer(
        bookSlug = workbook.target.slug,
        languageSlug = workbook.targetLanguageSlug,
        chapterCount = workbook.target.chapters.count().subscribeOn(Schedulers.io()).blockingGet(),
        chapterTitle = chapter.title,
        chapterSort = chapter.sort,
        chunkCount = chapter.chunks.count().subscribeOn(Schedulers.io()).blockingGet(),
        start = chunk?.start,
        end = chunk?.end,
        contentType = recordable.contentType,
        sort = recordable.sort,
        rcSlug = rcSlug
    )
}