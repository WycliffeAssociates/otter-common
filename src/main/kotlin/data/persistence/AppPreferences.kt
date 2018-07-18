package data.persistence

// interface to getting user-independent app preferences
interface AppPreferences {
    fun getCurrentUserHash() : String // should return an empty string if no current user hash
    fun putCurrentUserHash(userHash: String)
}