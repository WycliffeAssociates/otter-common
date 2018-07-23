package data

data class Content(
        var id: Int,
        var percentRecorded: Int,
        var percentEdited: Int,
        var percentCompleted: Int,
        var takes: MutableList<Take>
)