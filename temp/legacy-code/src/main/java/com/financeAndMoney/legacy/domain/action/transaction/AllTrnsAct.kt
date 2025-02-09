package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.transaction

import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.repository.TransactionRepository
import com.financeAndMoney.frp.action.FPAction
import javax.inject.Inject

class AllTrnsAct @Inject constructor(
    private val transactionRepository: TransactionRepository
) : FPAction<Unit, List<Transaction>>() {
    override suspend fun Unit.compose(): suspend () -> List<Transaction> = suspend {
        transactionRepository.findAll()
    }
}
