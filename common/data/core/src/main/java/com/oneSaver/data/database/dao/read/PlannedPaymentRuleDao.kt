package com.oneSaver.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.oneSaver.data.database.entities.ScheduledPaymentRuleEntity
import java.util.*

@Dao
interface PlannedPaymentRuleDao {
    @Query("SELECT * FROM planned_payment_rules WHERE isDeleted = 0 ORDER BY amount DESC, startDate ASC")
    suspend fun findAll(): List<ScheduledPaymentRuleEntity>

    @Query("SELECT * FROM planned_payment_rules WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<ScheduledPaymentRuleEntity>

    @Query(
        "SELECT * FROM planned_payment_rules WHERE isDeleted = 0 AND oneTime = :oneTime ORDER BY amount DESC, startDate ASC"
    )
    suspend fun findAllByOneTime(oneTime: Boolean): List<ScheduledPaymentRuleEntity>

    @Query("SELECT * FROM planned_payment_rules WHERE id = :id AND isDeleted = 0")
    suspend fun findById(id: UUID): ScheduledPaymentRuleEntity?

    @Query("SELECT COUNT(*) FROM planned_payment_rules WHERE isDeleted = 0 ")
    suspend fun countPlannedPayments(): Long
}
