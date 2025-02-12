package com.oneSaver.allStatus.domain.action.account

import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.Transaction
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.frp.action.FPAction
import com.oneSaver.allStatus.domain.pure.data.ClosedTimeRange
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
