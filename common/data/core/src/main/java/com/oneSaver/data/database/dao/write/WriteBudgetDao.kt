package com.oneSaver.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.oneSaver.data.database.entities.BudgetEntity
import java.util.UUID

@Dao
interface WriteBudgetDao {
    @Upsert
    suspend fun save(value: BudgetEntity)

    @Upsert
    suspend fun saveMany(value: List<BudgetEntity>)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}
