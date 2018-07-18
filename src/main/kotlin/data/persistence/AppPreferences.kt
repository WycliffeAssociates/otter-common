package data.persistence

// interface to getting user-independent app preferences
interface AppPreferences {
    fun getCurrentUserHash() : String
    fun setCurrentUserHash(userHash: String)
}