package org.wycliffeassociates.otter.common.data.workbook

import org.wycliffeassociates.otter.common.data.model.ContentType

data class Resource(
    val title: Component,
    val body: Component?
) {
    class Component(
        val sort: Int,
        val textItem: TextItem,
        val audio: AssociatedAudio,
        val contentType: ContentType
    )
}
