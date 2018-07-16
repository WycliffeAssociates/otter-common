package data

data class Language(
        var id: Int = 0,
        val slug: String,
        val name: String,
        val type: String,
        val anglicizedName: String
)