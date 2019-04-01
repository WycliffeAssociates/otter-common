package org.wycliffeassociates.otter.common.data.rxmodel

import io.reactivex.Observable

data class Chapter(
        override val id: Int,
        override val sort: Int,
        override val title: String,
        override val audio: AssociatedAudio,
        override val hasResources: Boolean,
        override val resources: Observable<Resource>,

        val progress: Observable<Int>,
        val chunks: Observable<Chunk>
) : BookElement
