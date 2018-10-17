package org.wycliffeassociates.otter.common.data.model

data class RelatedCollectionContent(
        val collection: Collection,
        val subcollections: List<RelatedCollectionContent>,
        val content: List<Chunk>
)