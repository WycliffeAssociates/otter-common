package org.wycliffeassociates.otter.common.data.model

data class Chunk(
        val sort: Int,
        var labelKey: String,
        val start: Int,
        val end: Int,
        var selectedTake: Take?,
        var id: Int = 0
)