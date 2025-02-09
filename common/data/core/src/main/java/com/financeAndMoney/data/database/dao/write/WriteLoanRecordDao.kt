package com.financeAndMoney.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.financeAndMoney.data.database.entities.MkopoRecordEntity
import java.util.UUID

@Dao
interface WriteLoanRecordDao {
    @Upsert
    suspend fun save(value: MkopoRecordEntity)

    @Upsert
    suspend fun saveMany(value: List<MkopoRecordEntity>)

    @Query("DELETE FROM loan_records WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM loan_records")
    suspend fun deleteAll()
}
