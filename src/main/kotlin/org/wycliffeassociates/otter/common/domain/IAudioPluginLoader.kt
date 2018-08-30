package org.wycliffeassociates.otter.common.domain

import io.reactivex.Observable
import org.wycliffeassociates.otter.common.data.model.IAudioPlugin
import java.io.File

interface IAudioPluginLoader {
    fun load(pluginFile: File): Observable<IAudioPlugin>
    fun loadAll(pluginDir: File): Observable<List<IAudioPlugin>>
}
