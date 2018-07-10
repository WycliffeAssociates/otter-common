package app.dataModel

import io.requery.Entity
import io.requery.Generated
import io.requery.Key
import io.requery.Persistable

@Entity
interface User: Persistable{

    @get:Key
    @get:Generated
    var _id: Int
    var hash: String
    var recordedNamePath: String
}

