package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.budget

import com.financeAndMoney.data.database.dao.read.BudgetDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.action.thenMap
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.Budget
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class BudgetsAct @Inject constructor(
    private val budgetDao: BudgetDao
) : FPAction<Unit, ImmutableList<Budget>>() {
    override suspend fun Unit.compose(): suspend () -> ImmutableList<Budget> = suspend {
        budgetDao.findAll()
    } thenMap { it.toLegacyDomain() } then { it.toImmutableList() }
}
