package data

data class UserPreferences(
        var preferredSourceLanguage: Language?,
        var preferredTargetLanguage: Language?,
        var dayNightMode: Enum<DayNight> = DayNight.DAY,
        var uILanguagePreferences: String = "en"
)

enum class DayNight{
    DAY, NIGHT, AUTO
}