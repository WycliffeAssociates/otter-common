package org.wycliffeassociates.otter.common.domain.plugins

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.wycliffeassociates.otter.common.persistence.repositories.IAudioPluginRepository

class InitializePlugins(
        private val pluginRepository: IAudioPluginRepository
) {
    fun init(): Completable = pluginRepository.initSelected()
}