package org.wycliffeassociates.otter.common.data.model

import java.io.File
import java.util.Date

data class Take(
        var id: Int = 0,
        var filename: String,
        var path: File,
        var number: Int,
        var timestamp: Date,
        var filePath: String,
        var isUnheard: Boolean
)