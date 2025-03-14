package com.oneSaver.allStatus.domain.deprecated.logic.model

import com.oneSaver.base.model.LoanRecordType
import com.oneSaver.legacy.datamodel.Account
import java.time.LocalDateTime

data class CreateLoanRecordData(
    val note: String?,
    val amount: Double,
    val dateTime: LocalDateTime,
    val interest: Boolean = false,
    val account: Account? = null,
    val createLoanRecordTransaction: Boolean = false,
    val convertedAmount: Double? = null,
    val loanRecordType: LoanRecordType
)
