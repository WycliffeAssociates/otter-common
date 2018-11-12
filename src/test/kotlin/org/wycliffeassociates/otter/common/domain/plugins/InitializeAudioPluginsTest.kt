package org.wycliffeassociates.otter.common.domain.plugins

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import org.junit.Test
import org.wycliffeassociates.otter.common.persistence.repositories.IAudioPluginRepository

class InitializeAudioPluginsTest {
    private val mockPluginRepository: IAudioPluginRepository = mock {
        on { initSelected() } doReturn Completable.complete()
    }

    // unit under test
    private val initializePlugins = InitializePlugins(mockPluginRepository)

    @Test
    fun shouldInitSelectedInRepository() {
        initializePlugins.init().test()
        verify(mockPluginRepository).initSelected()
        verifyNoMoreInteractions(mockPluginRepository)
    }
}