package data.model

class Book(
        id: Int = 0,
        titleKey: String,
        sort: Int,
        imagePath: String
) : Collection(id, "book", "anthology", titleKey, sort, imagePath)
