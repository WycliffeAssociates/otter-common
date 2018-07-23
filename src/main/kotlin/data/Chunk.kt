package data

data class Chunk(
        var id: Int,
        var start: Int,
        var end: Int,
        var sort: Int,
        var srcFile: String,
        var content: Content
)