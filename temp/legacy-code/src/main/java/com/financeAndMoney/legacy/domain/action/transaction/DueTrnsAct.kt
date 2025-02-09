package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.transaction

import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.repository.TransactionRepository
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.ClosedTimeRange
import javax.inject.Inject

class DueTrnsAct @Inject constructor(
    private val transactionRepository: TransactionRepository
) : FPAction<ClosedTimeRange, List<Transaction>>() {

    override suspend fun ClosedTimeRange.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionRepository.findAllDueToBetween(
                startDate = from,
                endDate = to
            )
        }
    }
}
