package com.financeAndMoney.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.financeAndMoney.data.database.entities.KategoriEntity
import java.util.UUID

@Dao
interface WriteCategoryDao {
    @Upsert
    suspend fun save(value: KategoriEntity)

    @Upsert
    suspend fun saveMany(values: List<KategoriEntity>)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}
