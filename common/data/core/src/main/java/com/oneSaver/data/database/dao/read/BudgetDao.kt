package com.oneSaver.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.oneSaver.data.database.entities.BajetiEntity
import java.util.*

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE isDeleted = 0 ORDER BY orderId ASC")
    suspend fun findAll(): List<BajetiEntity>

    @Query("SELECT * FROM budgets WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<BajetiEntity>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun findById(id: UUID): BajetiEntity?

    @Query("SELECT MAX(orderId) FROM budgets")
    suspend fun findMaxOrderNum(): Double?
}
