package org.wycliffeassociates.otter.common.domain.languages

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Single
import org.junit.Test
import org.wycliffeassociates.otter.common.persistence.repositories.ILanguageRepository

class GetLanguagesTest {
    private val mockLanguageRepository: ILanguageRepository = mock {
        on { getAll() } doReturn Single.just(listOf())
    }

    // UUT
    private val getLanguages = GetLanguages(mockLanguageRepository)

    @Test
    fun shouldGetAllFromRepository() {
        getLanguages.all().test()
        verify(mockLanguageRepository).getAll()
        verifyNoMoreInteractions(mockLanguageRepository)
    }
}