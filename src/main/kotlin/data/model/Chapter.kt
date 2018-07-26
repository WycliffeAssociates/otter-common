package data.model

class Chapter(
        id: Int = 0,
        titleKey: String,
        sort: Int,
        imagePath: String
) : Collection(id, "chapter", "book", titleKey, sort, imagePath)