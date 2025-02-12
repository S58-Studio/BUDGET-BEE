package com.oneSaver.data.database.dao.fake

import com.oneSaver.data.database.dao.read.PlannedPaymentRuleDao
import com.oneSaver.data.database.dao.write.WritePlannedPaymentRuleDao
import com.oneSaver.data.database.entities.ScheduledPaymentRuleEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakePlannedPaymentDao : PlannedPaymentRuleDao, WritePlannedPaymentRuleDao {
    private val items = mutableListOf<ScheduledPaymentRuleEntity>()

    override suspend fun findAll(): List<ScheduledPaymentRuleEntity> {
        return items
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<ScheduledPaymentRuleEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByOneTime(oneTime: Boolean): List<ScheduledPaymentRuleEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): ScheduledPaymentRuleEntity? {
        return items.find { it.id == id }
    }

    override suspend fun countPlannedPayments(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun save(value: ScheduledPaymentRuleEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<ScheduledPaymentRuleEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deletedByAccountId(accountId: UUID) {
        items.removeIf { it.accountId == accountId }
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}