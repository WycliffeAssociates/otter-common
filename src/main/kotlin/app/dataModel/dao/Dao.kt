package app.dataModel.dao

import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

interface Dao<T>{

    fun insert(obj: T): Observable<T>
    fun getById(id: Int): Observable<T>
    fun getAll(): Observable<T>
    fun update(obj: T): Completable
    fun deleteById(id:Int): Completable
}