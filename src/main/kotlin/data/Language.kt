package data

data class Language(
        var id: Int = 0,
        val slug: String,
        val name: String,
        val canBeSource: Boolean,
        val anglicizedName: String
)