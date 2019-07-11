package org.wycliffeassociates.otter.common.data.workbook

import com.jakewharton.rxrelay2.BehaviorRelay
import org.wycliffeassociates.otter.common.data.model.MimeType
import java.io.File
import java.time.LocalDate
import java.util.*

data class Take(
    val name: String,
    val file: File,
    val number: Int,
    val format: MimeType,
    val modifiedTimestamp: BehaviorRelay<LocalDate> = BehaviorRelay.createDefault(LocalDate.now()),
    val deletedTimestamp: BehaviorRelay<DateHolder> = BehaviorRelay.createDefault(DateHolder.empty)
) {
    override fun equals(other: Any?): Boolean {
        return (other as? Take)?.let {
            it.file == this.file
        } ?: false
    }

    override fun hashCode(): Int {
        return Objects.hashCode(this.file)
    }
}

data class DateHolder(val value: LocalDate?) {
    companion object {
        val empty = DateHolder(null)
        fun now() = DateHolder(LocalDate.now())
    }
}
