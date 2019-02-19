package org.wycliffeassociates.otter.common.domain.resourcecontainer.project

import java.io.BufferedReader
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class OtterZipFile(
        val absolutePath: String,
        private val rootZipFile: ZipFile,
        separator: String,
        val parentFile: OtterFile? = null,
        private val zipEntry: ZipEntry? = null
) {
    val isFile = zipEntry != null
    val nameWithoutExtension = absolutePath.split(separator, ".").dropLast(1).last() // TODO: Unit test or take the name between a possible slash and the last period
    val name: String = absolutePath.substring(absolutePath.lastIndexOf(separator) + 1) // TODO: Unit test. We aren't using prefix length

    fun bufferedReader(): BufferedReader = rootZipFile.getInputStream(zipEntry).bufferedReader()
    fun toRelativeString(parent: OtterFile): String = absolutePath.substringAfter(parent.absolutePath) // TODO: Unit test

    companion object {
        fun otterFileZ(
                absolutePath: String,
                rootZipFile: ZipFile,
                separator: String,
                parentFile: OtterFile? = null,
                zipEntry: ZipEntry? = null
        ): OtterFile {
            return OtterFile.Z(OtterZipFile(absolutePath, rootZipFile, separator, parentFile, zipEntry))
        }
    }
}

