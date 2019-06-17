package org.wycliffeassociates.otter.common.data.workbook

data class Workbook(
    val source: Book,
    val target: Book,
    val sourceLanguageSlug: String,
    val targetLanguageSlug: String
)
