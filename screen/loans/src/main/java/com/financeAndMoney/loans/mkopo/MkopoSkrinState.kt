package com.financeAndMoney.loans.mkopo

import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.loans.mkopo.data.DisplayMkopoo
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.LoanModalData
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
