package org.wycliffeassociates.otter.common.data.workbook

import io.reactivex.Observable

data class ResourceGroup(
    val info: ResourceContainerInfo,
    val resources: Observable<Resource>
)
