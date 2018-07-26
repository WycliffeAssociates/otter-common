package data.model

open class Collection (
        val id: Int = 0,
        val label: String,
        val belongsTo: String,
        val titleKey: String,
        val sort: Int,
        val imagePath: String
)