package com.oneSaver.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.oneSaver.data.database.entities.CategoryEntity
import java.util.UUID

@Dao
interface WriteCategoryDao {
    @Upsert
    suspend fun save(value: CategoryEntity)

    @Upsert
    suspend fun saveMany(values: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}
