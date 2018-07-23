package data

data class Book(
        var id: Int = 0,
        var title: String,
        var sort: Int,
        val Chapters: MutableList<Chapter>
)