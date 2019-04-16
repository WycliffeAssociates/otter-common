package org.wycliffeassociates.otter.common.data.workbook

import io.reactivex.Observable

interface HasChildBookElements {
    val children: Observable<BookElement>
    val subtreeResources: List<ResourceContainerInfo>
    //val progress: Observable<Int>
}
