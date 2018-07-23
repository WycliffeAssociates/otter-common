package data.model

data class Book(
        var id: Int = 0,
        val title: String,
        val sort: Int,
        val Chapters: MutableList<Chapter>
)