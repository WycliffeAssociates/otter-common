package org.wycliffeassociates.otter.common.domain.resourcecontainer.project

import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.domain.resourcecontainer.ImportResourceContainer
import org.wycliffeassociates.otter.common.domain.resourcecontainer.project.markdown.MarkdownProjectReader
import org.wycliffeassociates.otter.common.domain.resourcecontainer.project.usfm.UsfmProjectReader
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Project

interface IProjectReader {
    fun constructProjectTree(container: ResourceContainer, project: Project): Pair<ImportResourceContainer.Result, Tree>

    companion object {
        fun build(format: String): IProjectReader? = when(format.toLowerCase()) {
            "text/usfm" -> UsfmProjectReader()
            "text/markdown" -> MarkdownProjectReader()
            else -> null
        }
    }
}