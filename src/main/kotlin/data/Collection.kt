package data

data class Collection(
        var id: Int = 0,
        var title: String,
        var sort: Int,
        var label: String,
        var srcFile:String,
        val Language: MutableSet<Language>
)