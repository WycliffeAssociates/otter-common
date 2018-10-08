package org.wycliffeassociates.otter.common.domain.usfm

import java.io.File


val MARKER_BOOK_NAME = "\\id";
val MARKER_CHAPTER_NUMBER = "\\c";
val MARKER_VERSE_NUMBER = "\\v";
val MARKER_CHUNK = "\\s5"


var chapter = 1
val chapters: UsfmDocument = UsfmDocument()
data class Verse(val number: Int, val text: String)

class UsfmDocument : LinkedHashMap<Int, ArrayList<Verse>>()

fun parseUSFMFile(file: File): UsfmDocument {
    println(file.absolutePath)
    val reader = file.bufferedReader()
    reader.use {
        it.forEachLine {
            parseLine(it)
        }
    }
    return chapters
}

fun parseLine(line: String) {
    println(line)
    val split = line.split("\\s+".toRegex(), 2)
    if (split.isEmpty()) {
        return
    }
    when (split[0]) {
        MARKER_BOOK_NAME -> return
        MARKER_CHAPTER_NUMBER ->  {
            chapter = split[1]?.let {
                it.replace("\\s".toRegex(), "").toInt() //strip potential whitespace and convert to int
            }
            chapters[chapter] = arrayListOf<Verse>()
        }
        MARKER_VERSE_NUMBER -> {
            val sub = split[1].split("\\s+".toRegex(), 2)
            if(sub.size >= 2) {
                val number = sub[0].replace("\\s".toRegex(), "").toInt()
                val verse = sub[1]
                //list initialized on chapter tag parse
                chapters[chapter]!!.add(Verse(number, verse))
            }
        }
    }

}