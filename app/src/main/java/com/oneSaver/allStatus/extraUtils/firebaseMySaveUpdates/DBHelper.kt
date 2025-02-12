package com.oneSaver.allStatus.extraUtils.firebaseMySaveUpdates

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ AppUpdates::class], version = 1, exportSchema = false)
abstract class DBHelper : RoomDatabase() {

    companion object {
        private var INSTANCE: DBHelper? = null
        private var DATABASE_NAME = "Crimson.db"

        fun getInstance(context: Context): DBHelper {
            if(INSTANCE == null) {
                // Standard way to create singleton class
                synchronized(DBHelper::class.java) {
                    INSTANCE = Room.databaseBuilder(context, DBHelper::class.java, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build()
                }
            }

            return INSTANCE!!
        }
    }

    abstract fun getAppUpdatesDao(): AppUpdatesDao
}