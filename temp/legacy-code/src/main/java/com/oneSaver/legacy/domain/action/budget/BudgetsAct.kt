package com.oneSaver.allStatus.domain.action.budget

import com.oneSaver.data.database.dao.read.BudgetDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.action.thenMap
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.Budget
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
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
