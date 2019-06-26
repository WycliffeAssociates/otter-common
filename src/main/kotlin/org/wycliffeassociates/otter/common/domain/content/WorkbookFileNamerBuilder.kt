package org.wycliffeassociates.otter.common.domain.content

import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.data.workbook.*

object WorkbookFileNamerBuilder: FileNamer.Builder() {

    fun setWorkbookElements(
        workbook: Workbook,
        chapter: Chapter,
        chunk: Chunk?,
        recordable: Recordable,
        rcSlug: String
    ): WorkbookFileNamerBuilder {
        setWorkbook(workbook)
        setChapter(chapter)
        setChunk(chunk)
        setRecordable(recordable)
        this.rcSlug = rcSlug
        return this
    }

    private fun setWorkbook(workbook: Workbook) {
        bookSlug = workbook.target.slug
        languageSlug = workbook.targetLanguageSlug
        chapterCount = workbook.target.chapters.count().subscribeOn(Schedulers.io()).blockingGet()
    }

    private fun setChapter(chapter: Chapter) {
        chapterTitle = chapter.title
        chapterSort = chapter.sort
        chunkCount = chapter.chunks.count().subscribeOn(Schedulers.io()).blockingGet()
    }

    private fun setChunk(chunk: Chunk?) {
        start = chunk?.start
        end = chunk?.end
    }

    private fun setRecordable(recordable: Recordable) {
        contentType = recordable.contentType
        sort = recordable.sort
    }
}