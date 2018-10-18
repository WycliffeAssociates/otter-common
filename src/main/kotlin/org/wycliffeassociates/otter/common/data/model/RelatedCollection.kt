package org.wycliffeassociates.otter.common.data.model

data class RelatedCollection(
        val collection: Collection,
        val subcollections: List<RelatedCollection>,
        val content: List<Chunk>
)