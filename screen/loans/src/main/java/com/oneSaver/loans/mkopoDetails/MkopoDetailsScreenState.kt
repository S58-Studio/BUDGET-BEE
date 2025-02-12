package com.oneSaver.loans.mkopoDetails

import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.Loan
import com.oneSaver.loans.mkopo.data.DisplayMkopoRekodi
import com.oneSaver.allStatus.userInterface.theme.modal.LoanModalData
import com.oneSaver.allStatus.userInterface.theme.modal.LoanRecordModalData
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
