package org.wycliffeassociates.otter.common.data.persistence

import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.dao.LanguageDao
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.audioplugin.IAudioPlugin
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Project
import org.wycliffeassociates.otter.common.data.model.Take

interface AppDatabase {
    fun getLanguageDao(): LanguageDao
    fun getProjectDao(): Dao<Project>
    fun getCollectionDao(): Dao<Collection>
    fun getChunkDao(): Dao<Chunk>
    fun getTakesDao(): Dao<Take>
    fun getAudioPluginDao(): Dao<IAudioPlugin>
    fun getAudioPluginDataDao(): Dao<AudioPluginData>
}