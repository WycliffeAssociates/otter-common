package data.model

data class Anthology(
        var id: Int = 0,
        val title: String, // This will be a key
        val label: String,
        val sort: Int
)