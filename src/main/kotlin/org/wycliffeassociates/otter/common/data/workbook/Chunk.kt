package org.wycliffeassociates.otter.common.data.workbook

data class Chunk(
    override val sort: Int,
    override val audio: AssociatedAudio,
    override val resources: List<ResourceGroup>,

    val text: TextItem?,
    val start: Int,
    val end: Int

) : BookElement {
    override val title
        get() = start.toString()
}
