package org.wycliffeassociates.otter.common.domain

import java.io.File
import java.lang.RuntimeException
import java.util.zip.ZipFile

class UnZipResourceContainer(
        private val zippedContainer: File,
        private val destDirectory: File
) {
    // Adapted from https://stackoverflow.com/questions/46627357/unzip-a-file-in-kotlin-script-kts
    fun unzip(): File {
        val zipFile = ZipFile(zippedContainer)
        var root: File? = null
        zipFile
                .entries()
                .asSequence()
                .forEach {
                    if (it.isDirectory) {
                        val dir = destDirectory.resolve(it.name)
                        dir.mkdirs()
                        if (dir.parentFile == destDirectory) {
                            root = dir
                        }
                    } else {
                        val inputStream = zipFile.getInputStream(it)
                        val destination = destDirectory.resolve(File(it.name))
                        val outputStream = destination.outputStream()
                        inputStream.copyTo(outputStream)
                    }
                }
        return root ?: throw RuntimeException("No root RC folder found")
    }
}