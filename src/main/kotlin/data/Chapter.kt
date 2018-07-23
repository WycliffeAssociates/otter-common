package data

data class Chapter(
        var id: Int = 0,
        var title: String,
        var sort: Int,
        val Chunk: MutableList<Chunk>
)