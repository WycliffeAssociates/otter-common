package data

data class User(
        var id: Int = 0,
        var audioHash: String,
        val audioPath: String,
        val sourceLanguages: List<Language>,
        val targetLanguage: List<Language>,
        var preferredSourceLanguage: Language,
        var preferredTargetLanguage: Language
)



