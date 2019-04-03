package org.wycliffeassociates.otter.common.data.workbook

import io.reactivex.Observable

data class Book(
    override val sort: Int,
    override val title: String,
    override val hasResources: Boolean,
    override val resources: Observable<Resource>,

    val chapters: Observable<Chapter>
) : BookElement
