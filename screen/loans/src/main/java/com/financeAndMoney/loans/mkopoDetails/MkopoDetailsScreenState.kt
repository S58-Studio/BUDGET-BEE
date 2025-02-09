package com.financeAndMoney.loans.mkopoDetails

import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.loans.mkopo.data.DisplayMkopoRekodi
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.LoanModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.LoanRecordModalData
import kotlinx.collections.immutable.ImmutableList

data class MkopoDetailsScreenState(
    val baseCurrency: String,
    val loan: Loan?,
    val displayMkopoRekodis: ImmutableList<DisplayMkopoRekodi>,
    val loanTotalAmount: Double,
    val amountPaid: Double,
    val loanAmountPaid: Double,
    val accounts: ImmutableList<Account>,
    val selectedLoanAccount: Account?,
    val createLoanTransaction: Boolean,
    val loanModalData: LoanModalData?,
    val loanRecordModalData: LoanRecordModalData?,
    val waitModalVisible: Boolean,
    val isDeleteModalVisible: Boolean
)
