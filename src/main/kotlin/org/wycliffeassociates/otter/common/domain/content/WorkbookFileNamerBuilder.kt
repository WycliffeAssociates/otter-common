package org.wycliffeassociates.otter.common.domain.content

import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.data.workbook.*

class WorkbookFileNamerBuilder: FileNamer.Builder() {

    private var buildLevel: BuildLevel = BuildLevel.EMPTY

    enum class BuildLevel {
        EMPTY,
        WORKBOOK,
        CHAPTER,
        BOOK_ELEMENT,
        RECORDABLE;

        companion object {
            fun getOneLower(buildLevel: BuildLevel): BuildLevel = BuildLevel.values().first {
                it.ordinal == buildLevel.ordinal - 1
            }
            fun min(first: BuildLevel, second: BuildLevel): BuildLevel {
                return when (first.ordinal < second.ordinal) {
                    true -> first
                    else -> second
                }
            }
        }
    }

    override fun build(): FileNamer {
        checkBuildLevel()
        return super.build()
    }

    private fun checkBuildLevel() {
        if (buildLevel.ordinal != BuildLevel.values().map { it.ordinal }.max() ) {
            throw IllegalStateException("FileNamer cannot be built yet!")
        }
    }

    fun setWorkbook(workbook: Workbook?) {
        checkBuildLevelAndSet(BuildLevel.WORKBOOK, workbook) {
            bookSlug = workbook?.target?.slug
            languageSlug = workbook?.targetLanguageSlug
            chapterCount = workbook?.target?.chapters?.count()?.subscribeOn(Schedulers.io())?.blockingGet()
        }
    }

    fun setChapter(chapter: Chapter?) {
        checkBuildLevelAndSet(BuildLevel.CHAPTER, chapter) {
            chapterTitle = chapter?.title
            chapterSort = chapter?.sort
            chunkCount = chapter?.chunks?.count()?.subscribeOn(Schedulers.io())?.blockingGet()
        }
    }

    /**
     * Call this when the lowest-level book element is selected
     */
    fun setBookElement(bookElement: BookElement?) {
        checkBuildLevelAndSet(BuildLevel.BOOK_ELEMENT, bookElement) {
            when (bookElement) {
                is Chunk -> {
                    start = bookElement.start
                    end = bookElement.end
                }
                else -> {
                    start = null
                    end = null
                }
            }
        }
    }

    fun setRecordable(recordable: Recordable?) {
        checkBuildLevelAndSet(BuildLevel.RECORDABLE, recordable) {
            contentType = recordable?.contentType
            sort = recordable?.sort
        }
    }

    private fun checkBuildLevelAndSet(
        targetBuildLevel: BuildLevel,
        objectToSet: Any?,
        setFcn: () -> Unit
    ) {
        val oneLower = BuildLevel.getOneLower(targetBuildLevel)
        if (buildLevel.ordinal < oneLower.ordinal) {
            throw IllegalStateException("Expected build level $oneLower")
        }
        setFcn()
        buildLevel = when (objectToSet) {
            null -> BuildLevel.min(buildLevel, oneLower)
            else -> targetBuildLevel
        }
    }
}