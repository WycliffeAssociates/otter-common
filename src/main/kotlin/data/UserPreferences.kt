package data

data class UserPreferences(
        var id: Int = 0,
        var preferredSourceLanguage: Language,
        var preferredTargetLanguage: Language,
        var dayNightMode: Enum<DayNight> = DayNight.DAY,
        var uiLanguagePreferences: String = "en"
)

enum class DayNight{
    DAY, NIGHT, AUTO
}