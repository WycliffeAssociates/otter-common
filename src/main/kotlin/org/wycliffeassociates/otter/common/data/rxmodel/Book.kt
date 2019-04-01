package org.wycliffeassociates.otter.common.data.rxmodel

import io.reactivex.Observable

data class Book(
        val title: String,
        val chapters: Observable<Chapter>,
        val hasResources: Boolean,
        val progress: Observable<Int>,
        val sort: Int,
        val id: Int
)
