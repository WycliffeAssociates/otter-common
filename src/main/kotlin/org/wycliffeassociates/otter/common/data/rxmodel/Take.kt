package org.wycliffeassociates.otter.common.data.rxmodel

import io.reactivex.subjects.BehaviorSubject
import java.io.File

data class Take(
        val name: String,
        val file: File,
        val number: Int,
        val format: MimeType,
        val deleted: BehaviorSubject<Boolean>
)
