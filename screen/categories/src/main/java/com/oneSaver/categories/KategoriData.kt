package com.oneSaver.categories

import com.oneSaver.data.model.Category
import com.oneSaver.allStatus.domain.data.Reorderable

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
