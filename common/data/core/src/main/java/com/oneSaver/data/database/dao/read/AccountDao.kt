package com.oneSaver.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.oneSaver.data.database.entities.AkauntiEntity
import java.util.*

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE isDeleted = :deleted ORDER BY orderNum ASC")
    suspend fun findAll(deleted: Boolean = false): List<AkauntiEntity>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun findById(id: UUID): AkauntiEntity?

    @Query("SELECT MAX(orderNum) FROM accounts")
    suspend fun findMaxOrderNum(): Double?
}
