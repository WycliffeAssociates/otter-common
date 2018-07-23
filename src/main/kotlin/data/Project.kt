package data

data class Project(
        var id: Int = 0,
        val targetLanguage: Language,
        val sourceLanguage: Language,
        var title: String,
        val book: Book
)