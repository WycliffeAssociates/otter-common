package data.persistence

import data.Language
import data.User
import data.UserPreferences
import data.dao.Dao

interface TrDatabase {
     fun getUserDao(): Dao<User>
     fun getLanguageDao(): Dao<Language>
}