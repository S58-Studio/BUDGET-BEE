package com.oneSaver.loans.loans

import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.loans.loans.data.DisplayMkopoo
import com.oneSaver.allStatus.userInterface.theme.modal.LoanModalData
import kotlinx.collections.immutable.ImmutableList
import java.time.Instant

data class LoanScreenState(
    val baseCurrency: String,
    val completedLoans: ImmutableList<DisplayMkopoo>,
    val pendingLoans: ImmutableList<DisplayMkopoo>,
    val accounts: ImmutableList<Account>,
    val selectedAccount: Account?,
    val loanModalData: LoanModalData?,
    val reorderModalVisible: Boolean,
    val totalOweAmount: String,
    val totalOwedAmount: String,
    val paidOffLoanVisibility: Boolean,
    val dateTime: Instant,
    val selectedTab: LoanTab
)
