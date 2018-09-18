package org.wycliffeassociates.otter.common.data.model

import java.io.File
import java.util.Calendar

data class Take(
        var filename: String,
        var path: File,
        var number: Int,
        var timestamp: Calendar,
        var isUnheard: Boolean,
        var id: Int = 0
)