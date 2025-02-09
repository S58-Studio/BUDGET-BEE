package com.financeAndMoney.loans.mkopo.data

import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.legacy.utils.getDefaultFIATCurrency
import com.financeAndMoney.expenseAndBudgetPlanner.domain.data.Reorderable

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
