package com.financeAndMoney.expenseAndBudgetPlanner.appMigrations

interface Migration {
    val key: String

    suspend fun migrate()
}
