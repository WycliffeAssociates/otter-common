package data.model

data class Chunk(
        var id: Int,
        val start: Int,
        val end: Int,
        val sort: Int,
        val srcFile: String,
        var recorded: Boolean,
        var edited: Boolean,
        var completed: Boolean
)