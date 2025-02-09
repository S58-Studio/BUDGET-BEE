package com.financeAndMoney.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.financeAndMoney.data.database.entities.MkopoRecordEntity
import java.util.*

@Dao
interface LoanRecordDao {
    @Query("SELECT * FROM loan_records WHERE isDeleted = 0 ORDER BY dateTime DESC")
    suspend fun findAll(): List<MkopoRecordEntity>

    @Query("SELECT * FROM loan_records WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<MkopoRecordEntity>

    @Query("SELECT * FROM loan_records WHERE id = :id")
    suspend fun findById(id: UUID): MkopoRecordEntity?

    @Query("SELECT * FROM loan_records WHERE loanId = :loanId AND isDeleted = 0 ORDER BY dateTime DESC")
    suspend fun findAllByLoanId(loanId: UUID): List<MkopoRecordEntity>
}
