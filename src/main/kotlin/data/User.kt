package data

data class User(
        var id: Int = 0,
        var audioHash: String,
        val audioPath: String,
        val sourceLanguages: MutableList<Language>,
        val targetLanguages: MutableList<Language>,
        var userPreferences: UserPreferences
)



