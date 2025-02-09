package com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.util

fun Double?.nextOrderNum(): Double = this?.plus(1) ?: 0.0
