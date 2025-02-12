package com.oneSaver.budgets.model

import androidx.compose.runtime.Immutable
import com.oneSaver.legacy.datamodel.Budget
import com.oneSaver.allStatus.domain.data.Reorderable

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
