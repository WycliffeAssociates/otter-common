package org.wycliffeassociates.otter.common.domain.plugins

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import org.junit.Test
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import java.io.File

class ImportAudioPluginsTest {
    private val pluginDir = File(".")
    private val mockPluginRegistrar: IAudioPluginRegistrar = mock {
        on { importAll(any()) } doReturn Completable.complete()
    }
    private val mockDirectoryProvider: IDirectoryProvider = mock {
        on { audioPluginDirectory } doReturn pluginDir
    }

    // unit under test
    private val importAudioPlugins = ImportAudioPlugins(mockPluginRegistrar, mockDirectoryProvider)

    @Test
    fun shouldImportAllUsingRegistrar() {
        importAudioPlugins.importAll().test()
        verify(mockPluginRegistrar).importAll(pluginDir)
        verifyNoMoreInteractions(mockPluginRegistrar)
        verify(mockDirectoryProvider).audioPluginDirectory
        verifyNoMoreInteractions(mockDirectoryProvider)
    }

    // Unable to test import external since `copyTo` extension function cannot be mocked by Mockito
    // (constraint caused by Kotlin)
}
