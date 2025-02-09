package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home

import com.financeAndMoney.data.database.dao.read.TransactionDao
import com.financeAndMoney.frp.action.FPAction
import javax.inject.Inject

class HasTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<Unit, Boolean>() {
    override suspend fun Unit.compose(): suspend () -> Boolean = suspend {
        io {
            transactionDao.findAll_LIMIT_1().isNotEmpty()
        }
    }
}
