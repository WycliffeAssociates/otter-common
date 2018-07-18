package data

data class UserPreferences(
        var preferredSourceLanguage: Language,
        var preferredTargetLanguage: Language,
        var recordPluginName: String,
        var editPluginName: String,
        var dayNightMode: Enum<DayNight>,
        var uILanguagePreferences: String
)

enum class DayNight{
    DAY, NIGHT, AUTO
}