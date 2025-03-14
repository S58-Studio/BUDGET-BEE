package com.oneSaver.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.database.entities.TransactionEntity
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY dateTime DESC, dueDate ASC")
    suspend fun findAll(): List<TransactionEntity>

    @Suppress("FunctionNaming")
    @Deprecated("legacy remove")
    @Query("SELECT * FROM transactions WHERE isDeleted = 0 LIMIT 1")
    suspend fun findAll_LIMIT_1(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type ORDER BY dateTime DESC")
    suspend fun findAllByType(type: TransactionType): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and accountId = :accountId ORDER BY dateTime DESC"
    )
    suspend fun findAllByTypeAndAccount(
        type: TransactionType,
        accountId: UUID
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and accountId = :accountId and dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllByTypeAndAccountBetween(
        type: TransactionType,
        accountId: UUID,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and toAccountId = :toAccountId ORDER BY dateTime DESC"
    )
    suspend fun findAllTransfersToAccount(
        toAccountId: UUID,
        type: TransactionType = TransactionType.TRANSFER
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and toAccountId = :toAccountId and dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllTransfersToAccountBetween(
        toAccountId: UUID,
        startDate: Instant,
        endDate: Instant,
        type: TransactionType = TransactionType.TRANSFER
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllBetween(
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND accountId = :accountId AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllByAccountAndBetween(
        accountId: UUID,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllByCategoryAndBetween(
        categoryId: UUID,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId IS NULL) AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllUnspecifiedAndBetween(
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) AND type = :type AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID,
        type: TransactionType,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId IS NULL) AND type = :type AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllUnspecifiedAndTypeAndBetween(
        type: TransactionType,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND toAccountId = :toAccountId AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC"
    )
    suspend fun findAllToAccountAndBetween(
        toAccountId: UUID,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate ORDER BY dueDate ASC"
    )
    suspend fun findAllDueToBetween(
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND (categoryId = :categoryId) ORDER BY dateTime DESC, dueDate ASC"
    )
    suspend fun findAllDueToBetweenByCategory(
        startDate: Instant,
        endDate: Instant,
        categoryId: UUID
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND (categoryId IS NULL) ORDER BY dateTime DESC, dueDate ASC"
    )
    suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: Instant,
        endDate: Instant,
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND accountId = :accountId ORDER BY dateTime DESC, dueDate ASC"
    )
    suspend fun findAllDueToBetweenByAccount(
        startDate: Instant,
        endDate: Instant,
        accountId: UUID
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND recurringRuleId = :recurringRuleId ORDER BY dateTime DESC"
    )
    suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate AND type = :type ORDER BY dateTime DESC"
    )
    suspend fun findAllBetweenAndType(
        startDate: Instant,
        endDate: Instant,
        type: TransactionType
    ): List<TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate AND recurringRuleId = :recurringRuleId ORDER BY dateTime DESC"
    )
    suspend fun findAllBetweenAndRecurringRuleId(
        startDate: Instant,
        endDate: Instant,
        recurringRuleId: UUID
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun findById(id: UUID): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE id in (:ids)")
    suspend fun findByIds(ids: List<UUID>): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE isDeleted = 0 AND dateTime IS NOT null")
    suspend fun countHappenedTransactions(): Long

    // Smart Title Suggestions
    @Query("SELECT * FROM transactions WHERE title LIKE :pattern AND isDeleted = 0")
    suspend fun findAllByTitleMatchingPattern(pattern: String): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND isDeleted = 0")
    suspend fun countByTitleMatchingPattern(
        pattern: String,
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) ORDER BY dateTime DESC")
    suspend fun findAllByCategory(
        categoryId: UUID,
    ): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND categoryId = :categoryId AND isDeleted = 0")
    suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: UUID
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND accountId = :accountId ORDER BY dateTime DESC")
    suspend fun findAllByAccount(
        accountId: UUID
    ): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND accountId = :accountId AND isDeleted = 0")
    suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: UUID
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanId = :loanId AND loanRecordId IS NULL")
    suspend fun findLoanTransaction(
        loanId: UUID
    ): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanRecordId = :loanRecordId")
    suspend fun findLoanRecordTransaction(
        loanRecordId: UUID
    ): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanId = :loanId")
    suspend fun findAllByLoanId(
        loanId: UUID
    ): List<TransactionEntity>
}
