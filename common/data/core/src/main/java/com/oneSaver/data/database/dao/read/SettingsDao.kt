package com.oneSaver.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.oneSaver.data.database.entities.SettingsEntity
import java.util.*

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun findFirst(): SettingsEntity

    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun findFirstOrNull(): SettingsEntity?

    @Query("SELECT * FROM settings")
    suspend fun findAll(): List<SettingsEntity>

    @Query("SELECT * FROM settings WHERE id = :id")
    suspend fun findById(id: UUID): SettingsEntity?
}
