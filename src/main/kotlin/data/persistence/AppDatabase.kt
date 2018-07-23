package data.persistence

import data.model.Language
import data.model.Project
import data.model.User
import data.dao.Dao

interface AppDatabase {
     fun getUserDao(): Dao<User>
     fun getLanguageDao(): Dao<Language>
     fun getProjectDao(): Dao<Project>
}