package org.wycliffeassociates.otter.common.domain.mapper

import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.resourcecontainer.entity.dublincore
import java.io.File
import java.time.LocalDate

class DublinCoreMapperTest {
    private val mockLanguage: Language = mock()
    private val mockDir: File = mock()

    // unit under test
    val dublinCore = dublincore {
        conformsTo = "rc0.2"
        creator = "Wycliffe Associates"
        description = "A sample dublin core data set."
        format = "book"
        identifier = "ulb"
        issued = "2018-10-19"
        modified = "2018-10-21"
        publisher = "WA Publishing"
        subject = "Sample"
        type = "type"
        title = "Sample Data"
        version = "1.0.0"
    }

    private val expected = ResourceMetadata(
            "rc0.2",
            "Wycliffe Associates",
            "A sample dublin core data set.",
            "book",
            "ulb",
            LocalDate.of(2000, 7, 1),
            mockLanguage,
            LocalDate.of(2009, 1, 21),
            "WA Publishing",
            "Sample",
            "type",
            "Sample Data",
            "1.0.0",
            mockDir
    )

    @Test
    fun shouldMapToMetadata() {
        dublinCore.issued = "2000-07-01"
        dublinCore.modified = "2009-01-21"

        expected.issued = LocalDate.of(2000, 7, 1)
        expected.modified = LocalDate.of(2009, 1, 21)

        val result = dublinCore.mapToMetadata(mockDir, mockLanguage)
        Assert.assertEquals(expected, result)
    }


    @Test
    fun shouldHandleFullISODate() {
        dublinCore.issued = "2018-10-19T12:34:56.78-05:00"
        dublinCore.modified = "2019-05-01T09:05:06.8-05:00"

        expected.issued = LocalDate.of(2018, 10, 19)
        expected.modified = LocalDate.of(2019, 5, 1)

        val result = dublinCore.mapToMetadata(mockDir, mockLanguage)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldHandleNoDay() {
        dublinCore.issued = "2018-10"
        dublinCore.modified = "2019-05"

        expected.issued = LocalDate.of(2018, 10, 1)
        expected.modified = LocalDate.of(2019, 5, 1)

        val result = dublinCore.mapToMetadata(mockDir, mockLanguage)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldHandleNoMonth() {
        dublinCore.issued = "2018"
        dublinCore.modified = "2019"

        expected.issued = LocalDate.of(2018, 1, 1)
        expected.modified = LocalDate.of(2019, 1, 1)

        val result = dublinCore.mapToMetadata(mockDir, mockLanguage)
        Assert.assertEquals(expected, result)
    }
}