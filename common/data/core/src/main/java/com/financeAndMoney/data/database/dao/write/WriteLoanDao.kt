package com.financeAndMoney.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.financeAndMoney.data.database.entities.MkopoEntity
import java.util.UUID

@Dao
interface WriteLoanDao {
    @Upsert
    suspend fun save(value: MkopoEntity)

    @Upsert
    suspend fun saveMany(value: List<MkopoEntity>)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM loans")
    suspend fun deleteAll()
}
