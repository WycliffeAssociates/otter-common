package org.wycliffeassociates.otter.common.navigation

interface ITabGroupBuilder {
    fun build(type: TabGroupType): ITabGroup {
        return when (type) {
            TabGroupType.SELECT_PROJECT -> createSelectProjectTabGroup()
            TabGroupType.SELECT_CHAPTER -> createSelectChapterTabGroup()
            TabGroupType.SELECT_RECORDABLE -> createSelectRecordableTabGroup()
            TabGroupType.RECORD_SCRIPTURE -> createRecordScriptureTabGroup()
            TabGroupType.RECORD_RESOURCE -> createRecordResourceTabGroup()
        }
    }

    fun createSelectProjectTabGroup(): ITabGroup

    fun createSelectChapterTabGroup(): ITabGroup

    fun createSelectRecordableTabGroup(): ITabGroup

    fun createRecordScriptureTabGroup(): ITabGroup

    fun createRecordResourceTabGroup(): ITabGroup
}