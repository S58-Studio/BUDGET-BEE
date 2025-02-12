package com.oneSaver.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.oneSaver.data.database.entities.ScheduledPaymentRuleEntity
import java.util.UUID

@Dao
interface WritePlannedPaymentRuleDao {
    @Upsert
    suspend fun save(value: ScheduledPaymentRuleEntity)

    @Upsert
    suspend fun saveMany(value: List<ScheduledPaymentRuleEntity>)

    @Query("UPDATE planned_payment_rules SET isDeleted = 1, isSynced = 0 WHERE accountId = :accountId")
    suspend fun deletedByAccountId(accountId: UUID)

    @Query("DELETE FROM planned_payment_rules WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM planned_payment_rules")
    suspend fun deleteAll()
}
