package com.financeAndMoney.expenseAndBudgetPlanner.domain.data

interface Reorderable {
    fun getItemOrderNum(): Double

    fun withNewOrderNum(newOrderNum: Double): Reorderable
}
