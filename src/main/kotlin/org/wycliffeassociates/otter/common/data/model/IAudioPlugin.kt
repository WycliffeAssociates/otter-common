package org.wycliffeassociates.otter.common.data.model

import io.reactivex.Completable
import java.io.File

interface IAudioPlugin {
    var id: Int
    var name: String
    var version: String
    var canEdit: Boolean
    var canRecord: Boolean

    // Launch the plugin to edit/record the file specified
    fun launch(file: File): Completable
    // Delete the plugin from disk
    fun delete(): Completable
}