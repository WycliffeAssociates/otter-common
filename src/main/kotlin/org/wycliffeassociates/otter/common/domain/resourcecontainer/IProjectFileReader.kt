package org.wycliffeassociates.otter.common.domain.resourcecontainer

import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Project

interface IProjectFileReader {
    fun constructProjectTree(container: ResourceContainer, project: Project): Pair<ImportResourceContainer.Result, Tree>
}