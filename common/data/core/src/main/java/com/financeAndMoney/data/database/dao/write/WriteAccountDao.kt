package com.financeAndMoney.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.financeAndMoney.data.database.entities.AkauntiEntity
import java.util.UUID

@Dao
interface WriteAccountDao {
    @Upsert
    suspend fun save(value: AkauntiEntity)

    @Upsert
    suspend fun saveMany(values: List<AkauntiEntity>)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}
