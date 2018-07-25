package data.dao

import data.model.Language
import io.reactivex.Observable
import java.util.*

interface LanguageDao: Dao<Language>{
    fun getGatewayLanguage(): Observable<List<Language>>
}