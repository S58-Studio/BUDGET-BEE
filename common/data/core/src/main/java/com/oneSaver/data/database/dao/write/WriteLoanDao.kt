package com.oneSaver.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.oneSaver.data.database.entities.LoanEntity
import java.util.UUID

@Dao
interface WriteLoanDao {
    @Upsert
    suspend fun save(value: LoanEntity)

    @Upsert
    suspend fun saveMany(value: List<LoanEntity>)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM loans")
    suspend fun deleteAll()
}
