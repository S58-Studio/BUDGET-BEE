package com.oneSaver.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.oneSaver.data.database.entities.MkopoEntity
import java.util.*

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans WHERE isDeleted = 0 ORDER BY orderNum ASC")
    suspend fun findAll(): List<MkopoEntity>

    @Query("SELECT * FROM loans WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<MkopoEntity>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun findById(id: UUID): MkopoEntity?

    @Query("SELECT MAX(orderNum) FROM loans")
    suspend fun findMaxOrderNum(): Double?
}
