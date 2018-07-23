package data

data class Anthology(
        var id: Int = 0,
        val title: String,
        val sort: Int,
        val Languages: MutableSet<Language>,
        val Books: MutableList<Book>
)