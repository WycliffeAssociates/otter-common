package org.wycliffeassociates.otter.common.domain.plugins

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Test
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.common.persistence.repositories.IAudioPluginRepository

class AccessPluginsTest {
    private val mockPluginData: AudioPluginData = mock()

    private val mockPluginRepository: IAudioPluginRepository = mock {
        on { getAll() } doReturn Single.just(listOf())
        on { getEditorData() } doReturn Maybe.just(mock())
        on { getRecorderData() } doReturn Maybe.just(mock())
        on { setEditorData(mockPluginData) } doReturn Completable.complete()
        on { setRecorderData(mockPluginData) } doReturn Completable.complete()
        on { delete(mockPluginData) } doReturn Completable.complete()
    }

    // unit under test
    private val accessPlugins = AccessPlugins(mockPluginRepository)

    @Test
    fun shouldGetAllPluginDataFromRepository() {
        accessPlugins.getAllPluginData().test()
        verify(mockPluginRepository).getAll()
        verifyNoMoreInteractions(mockPluginRepository)
    }

    @Test
    fun shouldGetEditorDataFromRepository() {
        accessPlugins.getEditorData().test()
        verify(mockPluginRepository).getEditorData()
        verifyNoMoreInteractions(mockPluginRepository)
    }

    @Test
    fun shouldSetEditorDataInRepository() {
        accessPlugins.setEditorData(mockPluginData).test()
        verify(mockPluginRepository).setEditorData(mockPluginData)
        verifyNoMoreInteractions(mockPluginRepository)
    }

    @Test
    fun shouldGetRecorderDataFromRepository() {
        accessPlugins.getRecorderData().test()
        verify(mockPluginRepository).getRecorderData()
        verifyNoMoreInteractions(mockPluginRepository)
    }

    @Test
    fun shouldSetRecorderDataInRepository() {
        accessPlugins.setRecorderData(mockPluginData).test()
        verify(mockPluginRepository).setRecorderData(mockPluginData)
        verifyNoMoreInteractions(mockPluginRepository)
    }

    @Test
    fun shouldDeleteData() {
        accessPlugins.delete(mockPluginData).test()
        verify(mockPluginRepository).delete(mockPluginData)
        verifyNoMoreInteractions(mockPluginRepository)
    }
}