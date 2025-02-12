package com.oneSaver.loans.mkopo

import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.loans.mkopo.data.DisplayMkopoo
import com.oneSaver.allStatus.userInterface.theme.modal.LoanModalData
import kotlinx.collections.immutable.ImmutableList

data class MkopoSkrinState(
    val baseCurrency: String,
    val loans: ImmutableList<DisplayMkopoo>,
    val accounts: ImmutableList<Account>,
    val selectedAccount: Account?,
    val loanModalData: LoanModalData?,
    val reorderModalVisible: Boolean,
    val totalOweAmount: String,
    val totalOwedAmount: String,
    val paidOffLoanVisibility: Boolean,
)
