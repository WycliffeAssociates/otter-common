package org.wycliffeassociates.otter.common.domain.resourcecontainer.project

import java.io.BufferedReader
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class OtterZipFile(
        val absolutePath: String,
        private val rootZipFile: ZipFile,
        val parentFile: OtterZipFile? = null,
        private val zipEntry: ZipEntry? = null
) {
    val isFile = zipEntry != null
    val nameWithoutExtension = absolutePath.split(File.separator, ".").last() // TODO: Unit test
    val name: String = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1) // TODO: Unit test. We aren't using prefix length

    fun bufferedReader(): BufferedReader = rootZipFile.getInputStream(zipEntry).bufferedReader()
    fun toRelativeString(parent: OtterFile): String = absolutePath.substringAfter(parent.absolutePath) // TODO: Unit test
}

