package data

data class Content(
        var id: Int,
        var chunk: Chunk,
        var percentRecorded: Int,
        var percentEdited: Int,
        var percentCompleted: Int,
        val takes: MutableList<Take>
)