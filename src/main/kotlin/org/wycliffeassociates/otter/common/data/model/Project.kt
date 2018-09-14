package org.wycliffeassociates.otter.common.data.model

data class Project(
        var id: Int = 0,
        val source: Collection,
        val target: DerivedCollection
)