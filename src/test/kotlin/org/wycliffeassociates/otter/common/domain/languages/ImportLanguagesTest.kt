package org.wycliffeassociates.otter.common.domain.languages

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.persistence.repositories.ILanguageRepository

class ImportLanguagesTest {
    private val sourceFileStream = ClassLoader.getSystemResourceAsStream("langnames.json")
    private val mockLanguageRepository: ILanguageRepository = mock {
        on { insertAll(any()) } doReturn Single.just(listOf())
    }

    private val expectedLanguages = listOf(
            Language("en", "English", "English", "ltr", true),
            Language("fr", "Français", "French", "ltr", false)
    )

    // unit under test
    private val importLanguages = ImportLanguages(sourceFileStream, mockLanguageRepository)

    @Test
    fun shouldImportAllLanguages() {
        importLanguages.import().blockingAwait()

        argumentCaptor<List<Language>> {
            verify(mockLanguageRepository).insertAll(capture())
            verifyNoMoreInteractions(mockLanguageRepository)
            Assert.assertEquals(expectedLanguages, firstValue)
        }
    }
}