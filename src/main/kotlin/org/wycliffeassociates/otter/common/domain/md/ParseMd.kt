package org.wycliffeassociates.otter.common.domain.md

import java.io.BufferedReader

// TODO: Add Help type enum to HelpResource? (tn, tq)
data class HelpResource(var title: String, var body: String)

class HelpResourceList: ArrayList<HelpResource>()

class ParseMd {

    companion object {

        private val isTitleRegex = Regex("^#+\\s*[^#\\s]+")
        private val titleTextRegex = Regex("^#+\\s*")

        fun parse(reader: BufferedReader): HelpResourceList {

            val helpResourceList = HelpResourceList()

            reader.forEachLine {

                if (it.isEmpty())
                    return@forEachLine // continue

                // If we have a title, add a new help resource to the end of the list
                if (isTitleLine(it)) {

                    val titleText = getTitleText(it)
                    helpResourceList.add(HelpResource(titleText, ""))
                }
                // Found body text. Add the body to the help resource at the end of the list
                // If the list is empty, the body text will be discarded.
                else if (helpResourceList.size > 0) {

                    if (helpResourceList.last().body != "") {
                        helpResourceList.last().body += " "
                    }
                    helpResourceList.last().body += it
                }
            }

            return helpResourceList
        }

        internal fun getTitleText(line: String): String {

            return line.removePrefix(titleTextRegex.find(line)!!.value)
        }

        internal fun isTitleLine(line: String): Boolean {

            return isTitleRegex.containsMatchIn(line)
        }
    }
}