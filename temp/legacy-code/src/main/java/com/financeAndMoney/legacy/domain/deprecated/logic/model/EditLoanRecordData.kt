package com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model

import com.financeAndMoney.legacy.datamodel.LoanRecord

data class EditLoanRecordData(
    val newLoanRecord: LoanRecord,
    val originalLoanRecord: LoanRecord,
    val createLoanRecordTransaction: Boolean = false,
    val reCalculateLoanAmount: Boolean = false
)
