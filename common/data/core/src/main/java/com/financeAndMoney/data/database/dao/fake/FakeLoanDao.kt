package com.financeAndMoney.data.database.dao.fake

import com.financeAndMoney.data.database.dao.read.LoanDao
import com.financeAndMoney.data.database.dao.write.WriteLoanDao
import com.financeAndMoney.data.database.entities.MkopoEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeLoanDao : LoanDao, WriteLoanDao {
    private val items = mutableListOf<MkopoEntity>()

    override suspend fun findAll(): List<MkopoEntity> {
        return items
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<MkopoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): MkopoEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double? {
        return items.maxOfOrNull { it.orderNum }
    }

    override suspend fun save(value: MkopoEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<MkopoEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}