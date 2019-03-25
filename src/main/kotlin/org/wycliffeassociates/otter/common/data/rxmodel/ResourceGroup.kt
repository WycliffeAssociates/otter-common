package org.wycliffeassociates.otter.common.data.rxmodel

data class ResourceGroup(
        var resources: List<Resource>,
        var title: String
        // TODO: Icon?
)