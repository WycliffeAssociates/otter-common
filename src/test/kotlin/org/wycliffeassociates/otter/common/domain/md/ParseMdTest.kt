package org.wycliffeassociates.otter.common.domain.md

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.lang.AssertionError

class ParseMdTest {

    // These test cases are designed to test the creation of the HelpResource data objects
    // (including the branching logic of the parse() function)
    private val testParseCases = listOf(
            mapOf(
                    "lines" to listOf(
                            "# Title 1",
                            "",
                            "Body 1",
                            "",
                            "# Title 2",
                            "",
                            "Body 2"
                    ),
                    "expected" to arrayListOf(
                            HelpResource("Title 1", "Body 1"),
                            HelpResource("Title 2", "Body 2")
                    )
            ),
            mapOf(
                    "lines" to listOf(
                            "# Title 1",
                            "",
                            "Body 1",
                            "",
                            "Body 2", // Second line of body text
                            "",
                            "# Title 3",
                            "Body 3", // No space before body text
                            "",
                            "# Title 4", // Heading with no body text
                            "",
                            "# Title 5",
                            "",
                            "Body 5",
                            "# Title 6", // No space before title text
                            "",
                            "Body 6"
                    ),
                    "expected" to arrayListOf(
                            HelpResource("Title 1", "Body 1 Body 2"),
                            HelpResource("Title 3", "Body 3"),
                            HelpResource("Title 4", ""),
                            HelpResource("Title 5", "Body 5"),
                            HelpResource("Title 6", "Body 6")
                    )
            )
    )

    // Testing title text extraction
    private val testGetTitleTextCases = listOf(
            mapOf(
                    "line" to "# Hello",
                    "expected" to "Hello"
            ),
            mapOf(
                    "line" to "#  Matthew",
                    "expected" to "Matthew"
            ),
            mapOf(
                    "line" to "## John said",
                    "expected" to "John said"
            ),
            mapOf(
                    "line" to "# John said # hello",
                    "expected" to "John said # hello"
            ),
            mapOf(
                    "line" to "#John said hello",
                    "expected" to "John said hello"
            )
    )

    // Testing title recognition
    private val testIsTitleLineCases = listOf(
            mapOf(
                    "line" to "# Matthew",
                    "expected" to true
            ),
            mapOf(
                    "line" to "## Matthew",
                    "expected" to true
            ),
            mapOf(
                    "line" to "",
                    "expected" to false
            ),
            mapOf(
                    "line" to "Matthew # said hello",
                    "expected" to false
            ),
            mapOf(
                    "line" to "# ",
                    "expected" to false
            ),
            mapOf(
                    "line" to "#Matthew",
                    "expected" to true
            )
    )

    private fun checkLineOperatorFunction(input: String, output: Any, expected: Any) {

        try {
            assertEquals(expected, output)
        } catch (e: AssertionError) {
            println("Input: $input")
            println("Expected: $expected")
            println("Result: $output")
            throw e
        }
    }

    @Test
    fun testGetTitleText() {

        testGetTitleTextCases.forEach {

            val output = ParseMd.getTitleText(it["line"] as String)

            checkLineOperatorFunction(it["line"] as String, output, it["expected"] as String)
        }
    }

    @Test
    fun testIsTitleLine() {

        testIsTitleLineCases.forEach {

            val output = ParseMd.isTitleLine(it["line"] as String)

            checkLineOperatorFunction(it["line"] as String, output, it["expected"] as Boolean)
        }
    }

    private fun getBufferedReader(lines: List<String>): BufferedReader {

        val lineSeparator = System.lineSeparator()

        val stream: ByteArrayInputStream = lines.joinToString(lineSeparator).byteInputStream()

        return BufferedReader(stream.bufferedReader())
    }

    @Test
    fun testParse() {

        testParseCases.forEach {

            val expected = it["expected"]

            val bufferedReader = getBufferedReader(it["lines"] as List<String>)

            val helpResourceList = ParseMd.parse(bufferedReader)

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