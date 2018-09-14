package org.wycliffeassociates.otter.common.data.model

data class Project(
        var id: Int,
        val source: Collection,
        val target: Collection
)