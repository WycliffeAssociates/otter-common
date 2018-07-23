package data

data class Anthology(
        var id: Int = 0,
        var title: String,
        var sort: Int,
        val Languages: MutableSet<Language>,
        val Books: MutableList<Book>
)