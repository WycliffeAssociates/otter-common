package org.wycliffeassociates.otter.common.domain.resourcecontainer.project

import java.io.BufferedReader
import java.io.File

sealed class OtterFile {
    class F(val f: File) : OtterFile()
    class Z(val z: OtterZipFile) : OtterFile()

    val nameWithoutExtension: String
        get() = when (this) {
            is F -> f.nameWithoutExtension
            is Z -> z.nameWithoutExtension
        }

    val isFile: Boolean
        get() = when (this) {
            is F -> f.isFile
            is Z -> z.isFile
        }

    val parentFile: OtterFile?
        get() = when (this) {
            is F -> OtterFile.F(f.parentFile)
            is Z -> z.parentFile?.let { OtterFile.Z(it) }
        }

    val name: String
        get() = when (this) {
            is F -> f.name
            is Z -> z.name
        }

    val absolutePath: String
        get() = when (this) {
            is F -> f.absolutePath
            is Z -> z.absolutePath
        }

    fun bufferedReader(): BufferedReader = when (this) {
        is F -> f.bufferedReader()
        is Z -> z.bufferedReader()
    }

    fun toRelativeString(parentFile: OtterFile): String = when (this) {
        // Note: This cast should throw an exception if the cast fails because we should not
        // have a file with a parent file that is a zip file
        is F -> this.f.toRelativeString((parentFile as F).f)
        // We can have a zip file whose parent file is a file (directory)
        is Z -> this.z.toRelativeString(parentFile)
    }
}