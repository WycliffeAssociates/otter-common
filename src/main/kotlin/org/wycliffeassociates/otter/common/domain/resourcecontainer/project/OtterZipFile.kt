package org.wycliffeassociates.otter.common.domain.resourcecontainer.project

import java.io.BufferedReader
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class OtterZipFile(
        val parentFile: OtterZipFile,
        private val rootZipFile: ZipFile,
        val path: String,
        private val zipEntry: ZipEntry? = null
) {
    val isFile = zipEntry != null
    val nameWithoutExtension = nameWithoutExtension()
    val name = name()

    private fun nameWithoutExtension() = path.split(File.separator, ".").last() // TODO: Unit test
    fun bufferedReader(): BufferedReader = rootZipFile.getInputStream(zipEntry).bufferedReader()
    fun toRelativeString(parent: OtterZipFile): String = path.substringAfter(parent.path) // TODO: Unit test
    fun name(): String = path.substring(path.lastIndexOf(File.separator) + 1) // TODO: Unit test. We aren't using prefix length
}

sealed class OtterFile {
    class F(val f: File) : OtterFile()
    class Z(val z: OtterZipFile) : OtterFile()

//    val nameWithoutExtension: String = when (this) {
//        is F -> f.nameWithoutExtension
//        is Z -> z.nameWithoutExtension
//    }
    fun nameWithoutExtension(): String = when (this) {
        is F -> f.nameWithoutExtension
        is Z -> z.nameWithoutExtension
    }

//    val isFile: Boolean = when (this) {
//        is F -> f.isFile
//        is Z -> z.isFile
//    }
    fun isFile(): Boolean = when (this) {
        is F -> f.isFile
        is Z -> z.isFile
    }

    //    val parentFile: OtterFile = when (this) {
//        is F -> OtterFile.F(f.parentFile)
//        is Z -> OtterFile.Z(z.parentFile)
//    }
    fun parentFile(): OtterFile = when (this) {
        is F -> OtterFile.F(f.parentFile)
        is Z -> OtterFile.Z(z.parentFile)
    }

    //    val name: String = when (this) {
//        is F -> f.name
//        is Z -> z.name
//    }
    fun name(): String = when (this) {
        is F -> f.name
        is Z -> z.name
    }

    fun bufferedReader(): BufferedReader = when (this) {
        is F -> f.bufferedReader()
        is Z -> z.bufferedReader()
    }

    fun toRelativeString(parentFile: OtterFile): String = when (this) {
        // Note: Casting should throw an exception if the cast fails because we should not
        // be mixing Files and OtterZipFiles
        is F -> this.f.toRelativeString((parentFile as F).f)
        is Z -> this.z.toRelativeString((parentFile as Z).z)
    }
}