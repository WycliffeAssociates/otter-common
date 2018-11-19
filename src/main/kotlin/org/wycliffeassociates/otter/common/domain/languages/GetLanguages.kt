package org.wycliffeassociates.otter.common.domain.languages

import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.persistence.repositories.ILanguageRepository

class GetLanguages(private val languageRepository: ILanguageRepository) {
    fun all(): Single<List<Language>> {
        return languageRepository.getAll()
    }
}