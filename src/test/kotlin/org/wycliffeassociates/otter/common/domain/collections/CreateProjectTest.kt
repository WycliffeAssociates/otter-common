package org.wycliffeassociates.otter.common.domain.collections

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository

class CreateProjectTest {
    private val mockCollectionRepo: ICollectionRepository = mock {
        on { deriveProject(any(), any()) } doReturn Completable.complete()
    }

    // UUT
    private val createProject = CreateProject(mockCollectionRepo)

    @Test
    fun shouldUseCollectionRepositoryDeriveProject() {
        val mockMetadata: ResourceMetadata = mock()
        val mockSource: Collection = mock {
            on { resourceContainer } doReturn mockMetadata
        }
        val mockTargetLanguage: Language = mock()

        createProject.create(mockSource, mockTargetLanguage).test()

        verify(mockCollectionRepo).deriveProject(mockSource, mockTargetLanguage)
        verifyNoMoreInteractions(mockCollectionRepo)
    }

    @Test(expected = NullPointerException::class)
    fun shouldThrowNPEIfSourceHasNoMetadata() {
        val mockSource: Collection = mock()
        mockSource.resourceContainer = null

        val mockTargetLanguage: Language = mock()

        createProject.create(mockSource, mockTargetLanguage).test()
    }
}