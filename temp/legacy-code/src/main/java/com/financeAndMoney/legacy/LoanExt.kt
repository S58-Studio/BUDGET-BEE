package com.financeAndMoney.legacy

import com.financeAndMoney.base.legacy.stringRes
import com.financeAndMoney.data.model.LoanType
import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.core.userInterface.R

fun Loan.humanReadableType(): String {
    return if (type == LoanType.BORROW) {
        stringRes(R.string.borrowed_uppercase)
    } else {
        stringRes(R.string.lent_uppercase)
    }
}
