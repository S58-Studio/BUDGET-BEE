package com.financeAndMoney.budgets.model

import androidx.compose.runtime.Immutable
import com.financeAndMoney.legacy.datamodel.Budget
import com.financeAndMoney.expenseAndBudgetPlanner.domain.data.Reorderable

@Immutable
data class DisplayBajeti(
    val budget: Budget,
    val spentAmount: Double
) : Reorderable {
    override fun getItemOrderNum(): Double {
        return budget.orderId
    }

    override fun withNewOrderNum(newOrderNum: Double): Reorderable {
        return this.copy(
            budget = budget.copy(
                orderId = newOrderNum
            )
        )
    }
}
