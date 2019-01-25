package org.wycliffeassociates.otter.common.domain.md

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.lang.AssertionError

class ParseMdTest {

    val testCases = listOf(
            mapOf(
                    "id" to 1,
                    "lines" to listOf(
                        "# Title 1",
                        "",
                        "Text 1",
                        "",
                        "# Title 2",
                        "",
                        "Text 2"
                    ),
                    "expected" to arrayListOf(
                            HelpResource("Title 1", "Text 1"),
                            HelpResource("Title 2", "Text 2")
                    )
            )
    )

    private fun getBufferedReader(lines: List<String>): BufferedReader {

        // TODO: Is this right?
        val lineSeparator = System.lineSeparator()

        val stream: ByteArrayInputStream = lines.joinToString ( lineSeparator ).byteInputStream()

        return BufferedReader(stream.bufferedReader())
    }

    @Test
    fun testParse() {

        testCases.forEach{

            val expected = it["expected"]

            val bufferedReader = getBufferedReader(it["lines"] as List<String>)

            val helpResourceList = ParseMd().parse(bufferedReader)

//            val helpResourceList = HelpResourceList()
//            helpResourceList.add(HelpResource("Title 1", "Text 1"))
//            helpResourceList.add(HelpResource("Title 2", "Text 2"))

            try {
                assertEquals(expected, helpResourceList)
            } catch (e: AssertionError) {
                println("Input: " + it["lines"].toString())
                println("Expected: " + expected.toString())
                println("Result: " + helpResourceList.toString())
                throw e
            }
        }
    }
}