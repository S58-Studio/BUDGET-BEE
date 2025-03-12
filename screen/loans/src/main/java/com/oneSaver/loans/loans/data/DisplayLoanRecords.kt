package com.oneSaver.loans.loans.data

import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.LoanRecord

data class DisplayLoanRecords(
    val loanRecord: LoanRecord,
    val account: Account? = null,
    val loanRecordCurrencyCode: String = "",
    val loanCurrencyCode: String = "",
    val loanRecordTransaction: Boolean = false,
)
