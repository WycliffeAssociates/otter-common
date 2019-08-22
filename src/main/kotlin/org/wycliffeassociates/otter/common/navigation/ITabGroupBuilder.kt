package org.wycliffeassociates.otter.common.navigation

interface ITabGroupBuilder {
    fun build(type: TabGroupType): ITabGroup =
        when(type) {
            TabGroupType.APP -> createAppTabGroup()
            TabGroupType.WORKBOOK -> createWorkbookTabGroup()
            TabGroupType.ACTION -> createActionTabGroup()
            TabGroupType.RESOURCE_COMPONENT -> createResourceComponentTabGroup()
        }

    fun createAppTabGroup(): ITabGroup

    fun createWorkbookTabGroup(): ITabGroup

    fun createActionTabGroup(): ITabGroup

    fun createResourceComponentTabGroup(): ITabGroup
}