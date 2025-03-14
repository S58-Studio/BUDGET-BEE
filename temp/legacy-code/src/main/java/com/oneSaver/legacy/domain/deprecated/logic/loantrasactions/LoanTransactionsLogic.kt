package com.oneSaver.allStatus.domain.deprecated.logic.loantrasactions

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.legacy.utils.computationThread
import javax.inject.Inject

data class LoanTransactionsLogic @Inject constructor(
    val Loan: LTLoanMapper,
    val LoanRecord: LTLoanRecordMapper
) {
    suspend fun updateAssociatedLoanData(
        transaction: Transaction?,
        onBackgroundProcessingStart: suspend () -> Unit = {},
        onBackgroundProcessingEnd: suspend () -> Unit = {},
        accountsChanged: Boolean = true
    ) {
        computationThread {
            if (transaction == null) {
                return@computationThread
            }

            if (transaction.loanId != null && transaction.loanRecordId == null) {
                Loan.updateAssociatedLoan(
                    transaction = transaction,
                    onBackgroundProcessingStart = onBackgroundProcessingStart,
                    onBackgroundProcessingEnd = onBackgroundProcessingEnd,
                    accountsChanged = accountsChanged
                )
            } else if (transaction.loanId != null && transaction.loanRecordId != null) {
                LoanRecord.updateAssociatedLoanRecord(
                    transaction = transaction,
                    onBackgroundProcessingStart = onBackgroundProcessingStart,
                    onBackgroundProcessingEnd = onBackgroundProcessingEnd
                )
            }
        }
    }
}
