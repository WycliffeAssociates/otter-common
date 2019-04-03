package org.wycliffeassociates.otter.common.data.workbook

import io.reactivex.Observable

data class Chapter(
    override val sort: Int,
    override val title: String,
    override val hasResources: Boolean,
    override val resources: Observable<Resource>,
    override val audio: AssociatedAudio,

    val chunks: Observable<Chunk>
) : BookElementWithAudio
