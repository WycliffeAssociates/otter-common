package data.model

data class User(
        var id: Int = 0,
        val audioHash: String,
        val audioPath: String,
        val sourceLanguages: List<Language>,
        val targetLanguages: List<Language>,
        val userPreferences: UserPreferences
)



