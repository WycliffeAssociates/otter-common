package data.model

class Anthology(
        id: Int = 0,
        belongsTo: String,
        titleKey: String,
        sort: Int,
        imagePath: String
) : Collection(id, "anthology", belongsTo, titleKey, sort, imagePath)