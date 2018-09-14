package org.wycliffeassociates.otter.common.data.model

data class DerivedCollection(
        override var id: Int,
        override var sort: Int,
        override var labelKey: String,
        override var titleKey: String,
        var derivedFrom: Collection
) : Collection()