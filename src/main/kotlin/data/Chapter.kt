package data

data class Chapter(
        var id: Int = 0,
        val title: String,
        val sort: Int,
        val Chunk: MutableList<Chunk>
)