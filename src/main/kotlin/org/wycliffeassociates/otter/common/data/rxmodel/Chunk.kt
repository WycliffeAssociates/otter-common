package org.wycliffeassociates.otter.common.data.rxmodel

import io.reactivex.Observable

data class Chunk(
    override val sort: Int,
    override val title: String,
    override val hasResources: Boolean,
    override val resources: Observable<Resource>,
    override val audio: AssociatedAudio,

    val text: TextItem?
) : BookElementWithAudio
