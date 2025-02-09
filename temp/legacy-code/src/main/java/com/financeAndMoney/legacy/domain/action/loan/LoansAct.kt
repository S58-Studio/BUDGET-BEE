package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.loan

import com.financeAndMoney.data.database.dao.read.LoanDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.action.thenMap
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class LoansAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<Unit, ImmutableList<Loan>>() {
    override suspend fun Unit.compose(): suspend () -> ImmutableList<Loan> = suspend {
        loanDao.findAll()
    } thenMap { it.toLegacyDomain() } then { it.toImmutableList() }
}
