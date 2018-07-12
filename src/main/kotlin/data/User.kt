package data

data class User(
        var id: Int = 0,
        var audioHash: String,
        val audioPath: String,
        val sourceLanguages: MutableList<Language>,
        val targetLanguage: MutableList<Language>,
        var preferredSourceLanguage: Language,
        var preferredTargetLanguage: Language
)



