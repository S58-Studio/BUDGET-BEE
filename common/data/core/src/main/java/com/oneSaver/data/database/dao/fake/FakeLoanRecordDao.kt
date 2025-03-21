package com.oneSaver.data.database.dao.fake

import com.oneSaver.data.database.dao.read.LoanRecordDao
import com.oneSaver.data.database.dao.write.WriteLoanRecordDao
import com.oneSaver.data.database.entities.LoanRecordEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeLoanRecordDao : LoanRecordDao, WriteLoanRecordDao {
    private val items = mutableListOf<LoanRecordEntity>()

    override suspend fun findAll(): List<LoanRecordEntity> {
        return items
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<LoanRecordEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): LoanRecordEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<LoanRecordEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun save(value: LoanRecordEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<LoanRecordEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}