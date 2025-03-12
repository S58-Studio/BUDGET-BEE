package com.oneSaver.legacy

import com.oneSaver.base.legacy.stringRes
import com.oneSaver.base.model.LoanType
import com.oneSaver.legacy.datamodel.Loan
import com.oneSaver.core.userInterface.R

fun Loan.humanReadableType(): String {
    return if (type == LoanType.BORROW) {
        stringRes(R.string.borrowed_uppercase)
    } else {
        stringRes(R.string.lent_uppercase)
    }
}
