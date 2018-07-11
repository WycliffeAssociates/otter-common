package data.persistence

import data.User
import data.dao.Dao

interface TrDatabase {
     fun getUserDao(): Dao<User>
}