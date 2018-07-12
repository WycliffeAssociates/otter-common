package data

data class User(
        var id: Int = 0,
        var audioHash: String,
        val audioPath: String,
        var sourceLanguages: List<Language>,
        var targetLanguage: List<Language>,
        var preferredSourceLanguage: Language,
        var preferredTargetLanguage: Language
)



