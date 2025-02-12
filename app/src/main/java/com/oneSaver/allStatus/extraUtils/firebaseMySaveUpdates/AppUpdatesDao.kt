package com.oneSaver.allStatus.extraUtils.firebaseMySaveUpdates

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppUpdatesDao {

    @Insert
    fun addAppUpdates(updates: AppUpdates)

    @Query("SELECT * FROM app_updates LIMIT 1")
    fun getAppUpdates() : AppUpdates?

    @Query("DELETE FROM app_updates")
    fun clearUpdates()
}