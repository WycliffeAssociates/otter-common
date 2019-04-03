package org.wycliffeassociates.otter.common.data.rxmodel

import com.jakewharton.rxrelay2.BehaviorRelay
import java.io.File
import java.time.LocalDate

data class Take(
    val name: String,
    val file: File,
    val number: Int,
    val format: MimeType,
    val createdTimestamp: LocalDate,
    val deletedTimestamp: BehaviorRelay<Long?>
)
