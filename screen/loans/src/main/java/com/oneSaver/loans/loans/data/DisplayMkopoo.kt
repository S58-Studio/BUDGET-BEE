package com.oneSaver.loans.loans.data

import com.oneSaver.legacy.datamodel.Loan
import com.oneSaver.legacy.utils.getDefaultFIATCurrency
import com.oneSaver.allStatus.domain.data.Reorderable

data class DisplayMkopoo(
    val loan: Loan,
    val loanTotalAmount: Double,
    val amountPaid: Double,
    val currencyCode: String? = getDefaultFIATCurrency().currencyCode,
    val formattedDisplayText: String = "",
    val percentPaid: Double = 0.0
) : Reorderable {
    override fun getItemOrderNum(): Double {
        return loan.orderNum
    }

    override fun withNewOrderNum(newOrderNum: Double): Reorderable {
        return this.copy(
            loan = loan.copy(
                orderNum = newOrderNum
            )
        )
    }
}
