package org.wycliffeassociates.otter.common.data.rxmodel

import io.reactivex.Observable

interface BookElement {
    val id: Int
    val sort: Int
    val title: String
    val audio: AssociatedAudio
    val hasResources: Boolean
    val resources: Observable<Resource>
}
