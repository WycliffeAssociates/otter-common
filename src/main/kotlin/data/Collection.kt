package data

data class Collection(
        var id: Int,
        var title: String,
        var sort: Int,
        var label: String,
        var srcFile:String,
        val Language: Set<Language>
)