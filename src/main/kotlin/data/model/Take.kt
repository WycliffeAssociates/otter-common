package data.model

data class Take(
        var id: Int,
        val filePath: String,
        val sort: Int,
        var played: Boolean
)