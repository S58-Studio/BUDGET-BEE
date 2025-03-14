package com.oneSaver.allStatus.domain.deprecated.logic.model

import com.oneSaver.legacy.datamodel.LoanRecord

data class EditLoanRecordData(
    val newLoanRecord: LoanRecord,
    val originalLoanRecord: LoanRecord,
    val createLoanRecordTransaction: Boolean = false,
    val reCalculateLoanAmount: Boolean = false
)
