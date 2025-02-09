package com.financeAndMoney.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.financeAndMoney.data.database.entities.BajetiEntity
import java.util.UUID

@Dao
interface WriteBudgetDao {
    @Upsert
    suspend fun save(value: BajetiEntity)

    @Upsert
    suspend fun saveMany(value: List<BajetiEntity>)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}
