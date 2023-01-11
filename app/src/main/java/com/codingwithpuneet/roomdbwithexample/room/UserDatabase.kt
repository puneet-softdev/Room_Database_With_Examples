package com.codingwithpuneet.roomdbwithexample.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codingwithpuneet.roomdbwithexample.room.dao.UserDao
import com.codingwithpuneet.roomdbwithexample.room.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    private class UserDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.userDao())
                }
            }
        }
        suspend fun populateDatabase(userDao: UserDao) {
            userDao.deleteAll()
            val user1 = User(name = "Puneet Grover", address = "Karnal")
            userDao.insert(user1)
            val user2 = User(name = "Raman Kumar", address = "Panipat")
            userDao.insert(user2)
        }
    }


    companion object {
        private var DB_NAME = "user_database"
        private var INSTANCE: UserDatabase? = null
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(UserDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}