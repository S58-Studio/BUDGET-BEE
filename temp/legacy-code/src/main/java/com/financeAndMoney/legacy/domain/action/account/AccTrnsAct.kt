package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account

import com.financeAndMoney.data.model.AccountId
import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.repository.TransactionRepository
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.ClosedTimeRange
import java.util.UUID
import javax.inject.Inject

class AccTrnsAct @Inject constructor(
    private val transactionRepository: TransactionRepository
) : FPAction<AccTrnsAct.Input, List<Transaction>>() {
    override suspend fun Input.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionRepository.findAllByAccountAndBetween(
                accountId = AccountId(accountId),
                startDate = range.from,
                endDate = range.to
            ) + transactionRepository.findAllToAccountAndBetween(
                toAccountId = AccountId(accountId),
                startDate = range.from,
                endDate = range.to
            )
        }
    }

    class Input(
        val accountId: UUID,
        val range: ClosedTimeRange
    )
}
