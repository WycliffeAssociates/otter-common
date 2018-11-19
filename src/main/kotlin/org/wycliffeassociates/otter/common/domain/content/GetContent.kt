package org.wycliffeassociates.otter.common.domain.content

import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.repositories.IChunkRepository

class GetContent(
        private val chunkRepo: IChunkRepository
) {
    fun getChunks(collection: Collection): Single<List<Chunk>> {
        return chunkRepo.getByCollection(collection)
    }
}