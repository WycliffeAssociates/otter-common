package data.persistence

// interface to getting user-independent app preferences
interface AppPreferences {
    fun getCurrentUserId(): Int // should return an empty string if no current user hash
    fun setCurrentUserId(userId: Int)
}