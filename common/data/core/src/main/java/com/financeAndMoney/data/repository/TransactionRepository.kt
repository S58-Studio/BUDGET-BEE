package com.financeAndMoney.data.repository

import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.base.threading.DispatchersProvider
import com.financeAndMoney.data.database.dao.read.TransactionDao
import com.financeAndMoney.data.database.dao.write.WriteTransactionDao
import com.financeAndMoney.data.database.entities.TransactionEntity
import com.financeAndMoney.data.model.AccountId
import com.financeAndMoney.data.model.CategoryId
import com.financeAndMoney.data.model.Expense
import com.financeAndMoney.data.model.Income
import com.financeAndMoney.data.model.TagId
import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.model.TransactionId
import com.financeAndMoney.data.model.Transfer
import com.financeAndMoney.data.model.primitive.AssociationId
import com.financeAndMoney.data.model.primitive.NonNegativeLong
import com.financeAndMoney.data.model.primitive.toNonNegative
import com.financeAndMoney.data.repository.mapper.TransactionMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val mapper: TransactionMapper,
    private val transactionDao: TransactionDao,
    private val writeTransactionDao: WriteTransactionDao,
    private val dispatchersProvider: DispatchersProvider,
    private val tagRepository: TagRepository
) {
    suspend fun findAll(): List<Transaction> = withContext(dispatchersProvider.io) {
        val tagMap = async { findAllTagAssociations() }
        retrieveTrns(
            dbCall = transactionDao::findAll,
            retrieveTags = {
                tagMap.await()[it.id] ?: emptyList()
            }
        )
    }

    suspend fun findAllIncomeByAccount(
        accountId: AccountId
    ): List<Income> = retrieveTrns(
        dbCall = {
            transactionDao.findAllByTypeAndAccount(
                type = TransactionType.INCOME,
                accountId = accountId.value
            )
        }
    ).filterIsInstance<Income>()

    suspend fun findAllExpenseByAccount(
        accountId: AccountId
    ): List<Expense> = retrieveTrns(
        dbCall = {
            transactionDao.findAllByTypeAndAccount(
                type = TransactionType.EXPENSE,
                accountId = accountId.value
            )
        }
    ).filterIsInstance<Expense>()

    suspend fun findAllTransferByAccount(
        accountId: AccountId
    ): List<Transfer> = retrieveTrns(
        dbCall = {
            transactionDao.findAllByTypeAndAccount(
                type = TransactionType.TRANSFER,
                accountId = accountId.value
            )
        }
    ).filterIsInstance<Transfer>()

    suspend fun findAllTransfersToAccount(
        toAccountId: AccountId
    ): List<Transfer> = retrieveTrns(
        dbCall = {
            transactionDao.findAllTransfersToAccount(toAccountId = toAccountId.value)
        }
    ).filterIsInstance<Transfer>()

    suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = withContext(dispatchersProvider.io) {
        val transactions = transactionDao.findAllBetween(startDate, endDate)
        val tagAssociationMap = getTagsForTransactionIds(transactions)
        transactions.mapNotNull {
            val tags = tagAssociationMap[it.id] ?: emptyList()
            with(mapper) { it.toDomain(tags = tags) }.getOrNull()
        }
    }

    suspend fun findAllByAccountAndBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllByAccountAndBetween(
                accountId = accountId.value,
                startDate = startDate,
                endDate = endDate
            )
        }
    )

    suspend fun findAllToAccountAndBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllToAccountAndBetween(
                toAccountId = toAccountId.value,
                startDate = startDate,
                endDate = endDate
            )
        }
    )

    suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllDueToBetween(
                startDate = startDate,
                endDate = endDate
            )
        }
    )

    suspend fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: CategoryId
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllDueToBetweenByCategory(
                startDate = startDate,
                endDate = endDate,
                categoryId = categoryId.value
            )
        }
    )

    suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllDueToBetweenByCategoryUnspecified(
                startDate = startDate,
                endDate = endDate
            )
        }
    )

    suspend fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: AccountId
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllDueToBetweenByAccount(
                startDate = startDate,
                endDate = endDate,
                accountId = accountId.value
            )
        }
    )

    suspend fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID,
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllByCategoryAndTypeAndBetween(
                categoryId = categoryId,
                type = type,
                startDate = startDate,
                endDate = endDate
            )
        }
    )

    suspend fun findAllUnspecifiedAndTypeAndBetween(
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllUnspecifiedAndTypeAndBetween(
                type = type,
                startDate = startDate,
                endDate = endDate
            )
        }
    )

    suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllUnspecifiedAndBetween(
                startDate = startDate,
                endDate = endDate
            )
        }
    )

    suspend fun findAllByCategoryAndBetween(
        categoryId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllByCategoryAndBetween(
                categoryId = categoryId,
                startDate = startDate,
                endDate = endDate
            )
        }
    )

    suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllByRecurringRuleId(recurringRuleId)
        }
    )

    suspend fun findById(
        id: TransactionId
    ): Transaction? = withContext(dispatchersProvider.io) {
        transactionDao.findById(id.value)?.let {
            with(mapper) { it.toDomain() }.getOrNull()
        }
    }

    suspend fun findByIds(ids: List<TransactionId>): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            val tagMap = async { findTagsForTransactionIds(ids) }
            retrieveTrns(
                dbCall = {
                    transactionDao.findByIds(ids.map { it.value })
                },
                retrieveTags = {
                    tagMap.await()[it.id] ?: emptyList()
                }
            )
        }
    }

    suspend fun save(value: Transaction) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.save(
                with(mapper) { value.toEntity() }
            )
        }
    }

    suspend fun saveMany(value: List<Transaction>) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.saveMany(
                value.map { with(mapper) { it.toEntity() } }
            )
        }
    }

    suspend fun deleteById(id: TransactionId) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.deleteById(id.value)
        }
    }

    suspend fun deleteAllByAccountId(accountId: AccountId) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.deleteAllByAccountId(accountId.value)
        }
    }

    suspend fun deletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.deletedByRecurringRuleIdAndNoDateTime(recurringRuleId)
        }
    }

    suspend fun deleteAll() {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.deleteAll()
        }
    }

    suspend fun countHappenedTransactions(): NonNegativeLong = withContext(dispatchersProvider.io) {
        transactionDao.countHappenedTransactions().toNonNegative()
    }

    suspend fun findLoanTransaction(loanId: UUID): Transaction? =
        withContext(dispatchersProvider.io) {
            transactionDao.findLoanTransaction(loanId)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }

    suspend fun findLoanRecordTransaction(loanRecordId: UUID): Transaction? =
        withContext(dispatchersProvider.io) {
            transactionDao.findLoanRecordTransaction(loanRecordId)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }

    suspend fun findAllByLoanId(loanId: UUID): List<Transaction> = retrieveTrns(
        dbCall = {
            transactionDao.findAllByLoanId(loanId)
        }
    )

    private suspend fun retrieveTrns(
        dbCall: suspend () -> List<TransactionEntity>,
        retrieveTags: suspend (TransactionEntity) -> List<TagId> = { emptyList() },
    ): List<Transaction> = withContext(dispatchersProvider.io) {
        dbCall().mapNotNull {
            with(mapper) { it.toDomain(tags = retrieveTags(it)) }.getOrNull()
        }
    }

    private suspend fun getTagsForTransactionIds(
        transactions: List<TransactionEntity>
    ): Map<UUID, List<TagId>> {
        return findTagsForTransactionIds(transactions.map { TransactionId(it.id) })
    }

    private suspend fun findTagsForTransactionIds(
        transactionIds: List<TransactionId>
    ): Map<UUID, List<TagId>> {
        return tagRepository.findByAssociatedId(transactionIds.map { AssociationId(it.value) })
            .entries.associate {
                it.key.value to it.value.map { ta -> ta.id }
            }
    }

    private suspend fun findAllTagAssociations(): Map<UUID, List<TagId>> {
        return tagRepository.findByAllTagsForAssociations().entries.associate {
            it.key.value to it.value.map { ta -> ta.id }
        }
    }
}