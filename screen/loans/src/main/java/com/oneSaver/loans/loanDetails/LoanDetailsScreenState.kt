package com.oneSaver.loans.loanDetails

import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.Loan
import com.oneSaver.loans.loans.data.DisplayLoanRecords
import com.oneSaver.allStatus.userInterface.theme.modal.LoanModalData
import com.oneSaver.allStatus.userInterface.theme.modal.LoanRecordModalData
import kotlinx.collections.immutable.ImmutableList
import java.time.Instant

data class LoanDetailsScreenState(
    val baseCurrency: String,
    val loan: Loan?,
    val displayLoanRecords: ImmutableList<DisplayLoanRecords>,
    val loanTotalAmount: Double,
    val amountPaid: Double,
    val loanAmountPaid: Double,
    val accounts: ImmutableList<Account>,
    val selectedLoanAccount: Account?,
    val createLoanTransaction: Boolean,
    val loanModalData: LoanModalData?,
    val loanRecordModalData: LoanRecordModalData?,
    val waitModalVisible: Boolean,
    val isDeleteModalVisible: Boolean,
    val dateTime: Instant
)
