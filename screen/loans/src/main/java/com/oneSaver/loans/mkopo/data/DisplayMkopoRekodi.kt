package com.oneSaver.loans.mkopo.data

import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.LoanRecord

data class DisplayMkopoRekodi(
    val loanRecord: LoanRecord,
    val account: Account? = null,
    val loanRecordCurrencyCode: String = "",
    val loanCurrencyCode: String = "",
    val loanRecordTransaction: Boolean = false,
)
