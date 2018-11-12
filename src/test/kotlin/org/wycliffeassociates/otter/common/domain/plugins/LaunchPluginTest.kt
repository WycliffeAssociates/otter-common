package org.wycliffeassociates.otter.common.domain.plugins

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Maybe
import org.junit.Test
import org.wycliffeassociates.otter.common.data.audioplugin.IAudioPlugin
import org.wycliffeassociates.otter.common.persistence.repositories.IAudioPluginRepository
import java.io.File

class LaunchPluginTest {
    private val mockFile: File = mock()
    private val mockEditor: IAudioPlugin = mock {
        on { launch(any()) } doReturn Completable.complete()
    }
    private val mockRecorder: IAudioPlugin = mock {
        on { launch(any()) } doReturn Completable.complete()
    }

    private val mockPluginRepository: IAudioPluginRepository = mock {
        on { getEditor() } doReturn Maybe.just(mockEditor)
        on { getRecorder() } doReturn Maybe.just(mockRecorder)
    }

    // unit under test
    private val launchPlugin = LaunchPlugin(mockPluginRepository)

    @Test
    fun shouldLaunchEditor() {
        launchPlugin.launchEditor(mockFile).test()

        verify(mockPluginRepository).getEditor()
        verifyNoMoreInteractions(mockPluginRepository)
        verify(mockEditor).launch(mockFile)
        verifyNoMoreInteractions(mockEditor)
        verifyZeroInteractions(mockRecorder)
    }

    @Test
    fun shouldLaunchRecorder() {
        launchPlugin.launchRecorder(mockFile).test()

        verify(mockPluginRepository).getRecorder()
        verifyNoMoreInteractions(mockPluginRepository)
        verify(mockRecorder).launch(mockFile)
        verifyNoMoreInteractions(mockRecorder)
        verifyZeroInteractions(mockEditor)
    }

}