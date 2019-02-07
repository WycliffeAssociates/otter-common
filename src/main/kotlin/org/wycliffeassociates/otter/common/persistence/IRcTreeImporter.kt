package org.wycliffeassociates.otter.common.persistence

import io.reactivex.Completable
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.resourcecontainer.ResourceContainer

interface IRcTreeImporter {
    fun importResourceContainer(rc: ResourceContainer, rcTree: Tree, languageSlug: String): Completable
}