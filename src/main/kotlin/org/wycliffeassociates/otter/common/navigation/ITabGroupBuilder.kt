package org.wycliffeassociates.otter.common.navigation

interface ITabGroupBuilder {
    fun build(type: TabGroupType): ITabGroup {
        println("Building type $type")
        return when (type) {
            TabGroupType.APP -> createAppTabGroup()
            TabGroupType.CHOOSE_CHAPTER -> createChooseChapterTabGroup()
            TabGroupType.CHOOSE_RECORDABLE -> createChooseRecordableTabGroup()
            TabGroupType.RECORD_CHUNK -> createRecordChunkTabGroup()
            TabGroupType.RESOURCE_COMPONENT -> createResourceComponentTabGroup()
        }
    }

    fun createAppTabGroup(): ITabGroup

    fun createChooseChapterTabGroup(): ITabGroup

    fun createChooseRecordableTabGroup(): ITabGroup

    fun createRecordChunkTabGroup(): ITabGroup

    fun createResourceComponentTabGroup(): ITabGroup
}