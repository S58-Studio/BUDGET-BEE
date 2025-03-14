package com.oneSaver.allStatus.domain.deprecated.logic.loantrasactions

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.base.model.LoanRecordType
import com.oneSaver.base.model.TransactionType
import com.oneSaver.base.model.LoanType
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.Loan
import com.oneSaver.legacy.datamodel.LoanRecord
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.legacy.domain.deprecated.logic.loantrasactions.LoanTransactionsCore
import com.oneSaver.legacy.utils.computationThread
import com.oneSaver.legacy.utils.scopedIOThread
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateLoanData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.UUID
import javax.inject.Inject

class LTLoanMapper @Inject constructor(
    private val ltCore: LoanTransactionsCore
) {

    suspend fun createAssociatedLoanTransaction(data: CreateLoanData, loanId: UUID) {
        computationThread {
            ltCore.updateAssociatedTransaction(
                createTransaction = data.createLoanTransaction,
                loanId = loanId,
                amount = data.amount,
                loanType = data.type,
                selectedAccountId = data.account?.id,
                title = data.name,
                isLoanRecord = false,
                loanRecordType = LoanRecordType.DECREASE
            )
        }
    }

    suspend fun editAssociatedLoanTransaction(
        loan: Loan,
        createLoanTransaction: Boolean = false,
        transaction: Transaction?
    ) {
        computationThread {
            ltCore.updateAssociatedTransaction(
                createTransaction = createLoanTransaction,
                loanId = loan.id,
                amount = loan.amount,
                loanType = loan.type,
                selectedAccountId = loan.accountId,
                title = loan.name,
                isLoanRecord = false,
                transaction = transaction,
                time = transaction?.dateTime,
                loanRecordType = LoanRecordType.DECREASE
            )
        }
    }

    suspend fun deleteAssociatedLoanTransactions(loanId: UUID) {
        ltCore.deleteAssociatedTransactions(loanId = loanId)
    }

    suspend fun recalculateLoanRecords(
        oldLoanAccountId: UUID?,
        newLoanAccountId: UUID?,
        loanId: UUID
    ) {
        val accounts = ltCore.fetchAccounts().map { it.toLegacyDomain() }
        computationThread {
            if (oldLoanAccountId == newLoanAccountId || oldLoanAccountId.fetchAssociatedCurrencyCode(
                    accounts
                ) == newLoanAccountId.fetchAssociatedCurrencyCode(accounts)
            ) {
                return@computationThread
            }

            val newLoanRecords = calculateLoanRecords(
                loanId = loanId,
                newAccountId = newLoanAccountId,
            )

            ltCore.saveLoanRecords(newLoanRecords)
        }
    }

    suspend fun updateAssociatedLoan(
        transaction: Transaction?,
        onBackgroundProcessingStart: suspend () -> Unit = {},
        onBackgroundProcessingEnd: suspend () -> Unit = {},
        accountsChanged: Boolean = true
    ) {
        computationThread {
            transaction?.loanId ?: return@computationThread

            onBackgroundProcessingStart()

            val loan = ltCore.fetchLoan(transaction.loanId!!) ?: return@computationThread

            if (accountsChanged) {
                val newLoanRecords: List<LoanRecord> = calculateLoanRecords(
                    loanId = transaction.loanId!!,
                    newAccountId = transaction.accountId
                )
                ltCore.saveLoanRecords(newLoanRecords)
            }

            val modifiedLoan = loan.copy(
                amount = transaction.amount.toDouble(),
                name = if (transaction.title.isNullOrEmpty()) loan.name else transaction.title!!,
                type = if (transaction.type == TransactionType.INCOME) LoanType.BORROW else LoanType.LEND,
                accountId = transaction.accountId
            )

            ltCore.saveLoan(modifiedLoan.toLegacyDomain())
        }
        onBackgroundProcessingEnd()
    }

    private suspend fun calculateLoanRecords(
        newAccountId: UUID?,
        loanId: UUID
    ): List<LoanRecord> {
        return scopedIOThread { scope ->
            val loanRecords =
                ltCore.fetchAllLoanRecords(loanId = loanId)
                    .map { it.toLegacyDomain() }
                    .map { loanRecord ->
                        scope.async {
                            val convertedAmount: Double? =
                                ltCore.computeConvertedAmount(
                                    oldLoanRecordAccountId = loanRecord.accountId,
                                    oldLonRecordConvertedAmount = loanRecord.convertedAmount,
                                    oldLoanRecordAmount = loanRecord.amount,
                                    newLoanRecordAccountID = loanRecord.accountId,
                                    newLoanRecordAmount = loanRecord.amount,
                                    loanAccountId = newAccountId,
                                    accounts = ltCore.fetchAccounts().map { it.toLegacyDomain() },
                                )
                            loanRecord.copy(convertedAmount = convertedAmount)
                        }
                    }.awaitAll()
            loanRecords
        }
    }

    private suspend fun UUID?.fetchAssociatedCurrencyCode(accountsList: List<Account>): String {
        return ltCore.findAccount(accountsList, this)?.currency ?: ltCore.baseCurrency()
    }
}
