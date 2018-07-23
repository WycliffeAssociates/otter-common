package data

data class UserPreferences(
        var id: Int = 0,
        var preferredSourceLanguage: Language,
        var preferredTargetLanguage: Language,
        var uiLanguagePreferences: String = "en"
)
