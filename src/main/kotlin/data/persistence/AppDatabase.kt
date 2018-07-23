package data.persistence

import data.Language
import data.Project
import data.User
import data.UserPreferences
import data.dao.Dao

interface AppDatabase {
     fun getUserDao(): Dao<User>
     fun getLanguageDao(): Dao<Language>
     fun getProjectDao(): Dao<Project>
}