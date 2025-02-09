package com.financeAndMoney.data.database.dao.fake

import com.financeAndMoney.data.database.dao.read.LoanRecordDao
import com.financeAndMoney.data.database.dao.write.WriteLoanRecordDao
import com.financeAndMoney.data.database.entities.MkopoRecordEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeLoanRecordDao : LoanRecordDao, WriteLoanRecordDao {
    private val items = mutableListOf<MkopoRecordEntity>()

    override suspend fun findAll(): List<MkopoRecordEntity> {
        return items
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<MkopoRecordEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): MkopoRecordEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<MkopoRecordEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun save(value: MkopoRecordEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<MkopoRecordEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}