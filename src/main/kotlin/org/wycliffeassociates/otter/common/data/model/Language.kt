package org.wycliffeassociates.otter.common.data.model

data class Language(
        var id: Int,
        var slug: String,
        var name: String,
        var anglicizedName: String,
        var isRtl: Boolean,
        var isGateway: Boolean
)