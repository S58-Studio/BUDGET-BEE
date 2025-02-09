package com.financeAndMoney.loans.mkopo.data

import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.LoanRecord

data class DisplayMkopoRekodi(
    val loanRecord: LoanRecord,
    val account: Account? = null,
    val loanRecordCurrencyCode: String = "",
    val loanCurrencyCode: String = "",
    val loanRecordTransaction: Boolean = false,
)
