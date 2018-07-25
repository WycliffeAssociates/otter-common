package data.dao

import data.model.Language
import io.reactivex.Observable
import java.util.*

interface LanguageDao<T>: Dao<T>{
    fun getGatewayLanguage(): Observable<List<Language>>
}