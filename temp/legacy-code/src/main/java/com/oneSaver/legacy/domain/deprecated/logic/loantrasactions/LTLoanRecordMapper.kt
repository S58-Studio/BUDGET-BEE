package com.oneSaver.allStatus.domain.deprecated.logic.loantrasactions

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.legacy.datamodel.Loan
import com.oneSaver.legacy.datamodel.LoanRecord
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.legacy.domain.deprecated.logic.loantrasactions.LoanTransactionsCore
import com.oneSaver.legacy.utils.computationThread
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateLoanRecordData
import java.util.UUID
import javax.inject.Inject

class LTLoanRecordMapper @Inject constructor(
    private val ltCore: LoanTransactionsCore
) {
    suspend fun editAssociatedLoanRecordTransaction(
        loan: Loan,
        loanRecord: LoanRecord,
        createLoanRecordTransaction: Boolean,
    ) {
        computationThread {
            val transaction = ltCore.fetchLoanRecordTransaction(loanRecord.id)
            ltCore.updateAssociatedTransaction(
                createTransaction = createLoanRecordTransaction,
                loanRecordId = loanRecord.id,
                loanId = loan.id,
                amount = loanRecord.amount,
                loanType = loan.type,
                selectedAccountId = loanRecord.accountId,
                title = loanRecord.note,
                time = loanRecord.dateTime,
                isLoanRecord = true,
                transaction = transaction,
                loanRecordType = loanRecord.loanRecordType
            )
        }
    }

    suspend fun createAssociatedLoanRecordTransaction(
        loan: Loan,
        loanRecordId: UUID,
        data: CreateLoanRecordData,
    ) {
        computationThread {
            ltCore.updateAssociatedTransaction(
                createTransaction = data.createLoanRecordTransaction,
                loanType = loan.type,
                amount = data.amount,
                title = data.note,
                time = data.dateTime,
                loanRecordId = loanRecordId,
                loanId = loan.id,
                selectedAccountId = data.account?.id,
                isLoanRecord = true,
                loanRecordType = data.loanRecordType
            )
        }
    }

    suspend fun deleteAssociatedLoanRecordTransaction(loanRecordId: UUID) {
        ltCore.deleteAssociatedTransactions(loanRecordId = loanRecordId)
    }

    suspend fun updateAssociatedLoanRecord(
        transaction: Transaction?,
        onBackgroundProcessingStart: suspend () -> Unit = {},
        onBackgroundProcessingEnd: suspend () -> Unit = {},
    ) {
        transaction?.loanId ?: return
        transaction.loanRecordId ?: return
        computationThread {
            onBackgroundProcessingStart()

            val loanRecord =
                ltCore.fetchLoanRecord(transaction.loanRecordId!!) ?: return@computationThread
            val loan = ltCore.fetchLoan(transaction.loanId!!) ?: return@computationThread

            val convertedAmount = ltCore.computeConvertedAmount(
                oldLoanRecordAccountId = loanRecord.accountId,
                oldLonRecordConvertedAmount = loanRecord.convertedAmount,
                oldLoanRecordAmount = loanRecord.amount,
                newLoanRecordAccountID = transaction.accountId,
                newLoanRecordAmount = transaction.amount.toDouble(),
                loanAccountId = loan.accountId,
                accounts = ltCore.fetchAccounts().map { it.toLegacyDomain() }
            )

            val modifiedLoanRecord = loanRecord.copy(
                amount = transaction.amount.toDouble(),
                note = transaction.title,
                dateTime = transaction.dateTime ?: loanRecord.dateTime,
                accountId = transaction.accountId,
                convertedAmount = convertedAmount
            )
            ltCore.saveLoanRecords(modifiedLoanRecord.toLegacyDomain())
        }
        onBackgroundProcessingEnd()
    }

    suspend fun calculateConvertedAmount(
        loanAccountId: UUID?,
        newLoanRecord: LoanRecord,
        oldLoanRecord: LoanRecord,
        reCalculateLoanAmount: Boolean = false,
    ): Double? {
        return ltCore.computeConvertedAmount(
            oldLoanRecordAccountId = oldLoanRecord.accountId,
            oldLonRecordConvertedAmount = oldLoanRecord.convertedAmount,
            oldLoanRecordAmount = oldLoanRecord.amount,
            newLoanRecordAccountID = newLoanRecord.accountId,
            newLoanRecordAmount = newLoanRecord.amount,
            loanAccountId = loanAccountId,
            accounts = ltCore.fetchAccounts().map { it.toLegacyDomain() },
            reCalculateLoanAmount = reCalculateLoanAmount
        )
    }

    suspend fun calculateConvertedAmount(
        data: CreateLoanRecordData,
        loanAccountId: UUID?,
    ): Double? {
        return ltCore.computeConvertedAmount(
            oldLoanRecordAccountId = null,
            oldLonRecordConvertedAmount = null,
            oldLoanRecordAmount = 0.0,
            newLoanRecordAccountID = data.account?.id,
            newLoanRecordAmount = data.amount,
            loanAccountId = loanAccountId,
            accounts = ltCore.fetchAccounts().map { it.toLegacyDomain() },
        )
    }
}
