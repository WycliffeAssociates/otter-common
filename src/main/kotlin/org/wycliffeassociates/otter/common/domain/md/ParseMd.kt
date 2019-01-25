package org.wycliffeassociates.otter.common.domain.md

import java.io.BufferedReader

data class HelpResource(var title: String, var text: String)

class HelpResourceList: ArrayList<HelpResource>()

// TODO: make companion object?
// TODO: Check if there is a #?
// TODO: Help type enums? (tn, tq)
class ParseMd {

    val helpResourceList = HelpResourceList()

    fun parse(reader: BufferedReader): HelpResourceList {

        reader.useLines {
            // Each resource uses 4 lines: snippet/question, empty line, note/answer, empty line
            parseFromSequence(it.chunked(4))
        }

        return helpResourceList
    }

    private fun parseFromSequence(sequence: Sequence<List<String>>) {

        sequence.forEach {

            // Assume each snippet/question starts with "# ", so take the substring starting after this prefix
            val title = it.get(0).substring(2)

            // Snippets/questions and notes/answers are separated by one line, so skip a line
            val text = it.get(2)

            helpResourceList.add(HelpResource(title, text))
        }
    }
}