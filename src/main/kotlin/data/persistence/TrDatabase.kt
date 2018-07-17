package data.persistence

import data.Language
import data.User
import data.dao.Dao

interface TrDatabase {
     fun getUserDao(): Dao<User>
     fun geLanguageDao(): Dao<Language>
}