package data.persistence

// interface to getting user-independent app preferences
interface AppPreferences {
    fun getCurrentUserId() : String // should return an empty string if no current user hash
    fun setCurrentUserId(userHash: String)
}