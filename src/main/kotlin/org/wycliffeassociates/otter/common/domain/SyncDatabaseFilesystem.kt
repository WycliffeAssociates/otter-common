package org.wycliffeassociates.otter.common.domain

import io.reactivex.Completable
import org.wycliffeassociates.otter.common.persistence.repositories.ITakeRepository

class SyncDatabaseFilesystem(
        private val takeRepository: ITakeRepository
) {
    fun removeNonExistentTakes(): Completable {
        return takeRepository.removeNonExistentTakes()
    }
}