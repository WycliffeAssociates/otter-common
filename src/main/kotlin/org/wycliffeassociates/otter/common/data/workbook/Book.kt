package org.wycliffeassociates.otter.common.data.workbook

import io.reactivex.Observable
import io.reactivex.rxkotlin.cast

data class Book(
    val sort: Int,
    val title: String,
    val chapters: Observable<Chapter>,

    override val subtreeResources: List<ResourceContainerInfo>

): HasChildBookElements {

    override val children: Observable<BookElement> = chapters.cast()

}
