package com.financeAndMoney.data.database.dao.fake

import com.financeAndMoney.data.database.dao.read.BudgetDao
import com.financeAndMoney.data.database.dao.write.WriteBudgetDao
import com.financeAndMoney.data.database.entities.BajetiEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeBudgetDao : BudgetDao, WriteBudgetDao {
    private val items = mutableListOf<BajetiEntity>()

    override suspend fun findAll(): List<BajetiEntity> {
        return items
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<BajetiEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): BajetiEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double? {
        return items.maxOfOrNull { it.orderId }
    }

    override suspend fun save(value: BajetiEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<BajetiEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}