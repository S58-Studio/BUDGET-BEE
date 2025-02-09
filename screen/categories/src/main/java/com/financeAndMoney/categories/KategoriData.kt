package com.financeAndMoney.categories

import com.financeAndMoney.data.model.Category
import com.financeAndMoney.expenseAndBudgetPlanner.domain.data.Reorderable

data class KategoriData(
    val category: Category,
    val monthlyBalance: Double,
    val monthlyExpenses: Double,
    val monthlyIncome: Double
) : Reorderable {
    override fun getItemOrderNum() = category.orderNum

    override fun withNewOrderNum(newOrderNum: Double) = this.copy(
        category = category.copy(
            orderNum = newOrderNum
        )
    )
}
