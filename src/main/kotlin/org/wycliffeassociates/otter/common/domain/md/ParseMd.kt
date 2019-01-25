package org.wycliffeassociates.otter.common.domain.md

import java.io.File

data class HelpResource(var snippet: String, var helpText: String)

class HelpResourceList: ArrayList<HelpResource>()

// TODO: make companion object?
// TODO: Check if there is a #?
// TODO: Help type enums? (tn, tq)
class ParseMd(val file: File) {

    val helpResourceList = HelpResourceList()

    fun parse(): HelpResourceList {
        val reader = file.bufferedReader()
        reader.useLines {
            // Each resource uses 4 lines: snippet, empty line, text, empty line
            parseFromSequence(it.chunked(4))
        }

        return helpResourceList
    }

    private fun parseFromSequence(sequence: Sequence<List<String>>) {

        sequence.forEach {

            // Assume each snippet starts with "# ", so take the substring starting after this prefix
            val snippet = it.get(0).substring(2)

            // Snippets and text are separated by one line, so skip a line
            val helpText = it.get(2)

            helpResourceList.add(HelpResource(snippet, helpText))
        }
    }
}