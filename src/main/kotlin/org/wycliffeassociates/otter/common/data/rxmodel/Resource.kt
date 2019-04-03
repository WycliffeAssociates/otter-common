package org.wycliffeassociates.otter.common.data.rxmodel

data class Resource(
    val sort: Int,
    val title: TextItem,
    val body: TextItem?,
    val titleAudio: AssociatedAudio,
    val bodyAudio: AssociatedAudio?
)
