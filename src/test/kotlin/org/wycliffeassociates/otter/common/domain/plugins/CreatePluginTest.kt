package org.wycliffeassociates.otter.common.domain.plugins

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.common.persistence.repositories.IAudioPluginRepository

class CreatePluginTest {
    val mockData: AudioPluginData = mock()
    private val mockPluginRepository: IAudioPluginRepository = mock {
        on { insert(mockData) } doReturn Single.just(0)
        on { initSelected() } doReturn Completable.complete()
    }

    // unit under test
    private val createPlugin = CreatePlugin(mockPluginRepository)

    @Test
    fun shouldInsertPluginAndInit() {
        createPlugin.create(mockData).test()
        verify(mockPluginRepository).insert(mockData)
        verify(mockPluginRepository).initSelected()
        verifyNoMoreInteractions(mockPluginRepository)
    }
}