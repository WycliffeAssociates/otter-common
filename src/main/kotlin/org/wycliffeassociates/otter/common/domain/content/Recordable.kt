package org.wycliffeassociates.otter.common.domain.content

import org.wycliffeassociates.otter.common.data.model.ContentType
import org.wycliffeassociates.otter.common.data.workbook.*
import java.util.Objects

interface Recordable {
    val textItem: TextItem
    val audio: AssociatedAudio
    val contentType: ContentType
    val sort: Int

    fun equalsRecordable(other: Recordable): Boolean {
        return this.textItem.text == other.textItem.text
                && this.sort == other.sort
                && this.contentType == other.contentType
    }

    fun hashCodeRecordable(): Int {
        return Objects.hash(textItem.text, sort, contentType)
    }
}
