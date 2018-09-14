package org.wycliffeassociates.otter.common.data.model

data class Language(
        var id: Int = 0,
        val slug: String,
        val name: String,
        val anglicizedName: String,
        val isRtl: Boolean,
        val isGateway: Boolean
)