package data

data class Project(
        var id: Int = 0,
        val targetLanguage: Language,
        val sourceLanguage: Language,
        val book: Book,
        val content: MutableSet<Content>
)