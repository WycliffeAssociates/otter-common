package data.model

data class Anthology(
        var id: Int = 0,
        val title: String,
        val sort: Int,
        val Languages: MutableSet<Language>
)