package com.oneSaver.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.oneSaver.data.database.entities.CategoryEntity
import java.util.*

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isDeleted = :deleted ORDER BY orderNum ASC")
    suspend fun findAll(deleted: Boolean = false): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun findById(id: UUID): CategoryEntity?

    @Query("SELECT MAX(orderNum) FROM categories")
    suspend fun findMaxOrderNum(): Double?
}
