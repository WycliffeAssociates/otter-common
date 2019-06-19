package org.wycliffeassociates.otter.common.domain.content

import org.wycliffeassociates.otter.common.data.model.ContentType
import org.wycliffeassociates.otter.common.data.workbook.*
import java.util.Objects

class Recordable private constructor(
    val textItem: TextItem,
    val audio: AssociatedAudio,
    val start: Int? = null,
    val end: Int? = null,
    val sort: Int? = null,
    val contentType: ContentType? = null
) {
    companion object {
        fun build(bookElement: BookElement, component: Resource.Component): Recordable {
            return when (bookElement) {
                is Chapter -> Recordable(
                    textItem = component.textItem,
                    audio = component.audio,
                    sort = component.sort,
                    contentType = component.contentType
                )
                is Chunk -> Recordable(
                    textItem = component.textItem,
                    audio = component.audio,
                    start = bookElement.start,
                    end = bookElement.end,
                    sort = component.sort,
                    contentType = component.contentType
                )
                else -> throw IllegalStateException("Unsupported book element type found in Recordable.build")
            }
        }

        fun build(chunk: Chunk): Recordable? =
            chunk.text?.let {
                Recordable(
                    textItem = chunk.text,
                    audio = chunk.audio,
                    start = chunk.start,
                    end = chunk.end
                )
            }
    }

    override fun equals(other: Any?): Boolean {
        return (other as? Recordable)?.let {
            this.textItem.text == other.textItem.text
                    && this.start == other.start
                    && this.end == other.end
                    && this.sort == other.sort
                    && this.contentType == other.contentType
        } ?: false
    }

    override fun hashCode(): Int {
        return Objects.hash(textItem.text, start, end, sort, contentType)
    }
}
