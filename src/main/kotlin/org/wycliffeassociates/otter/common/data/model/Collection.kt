package org.wycliffeassociates.otter.common.data.model

data class Collection(
        var id: Int,
        var sort: Int,
        var slug: String,
        var labelKey: String,
        var titleKey: String,
        var resourceContainer: ResourceContainer
)