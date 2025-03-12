package com.oneSaver.legacy.domain.action.transaction

import com.oneSaver.data.model.Transaction
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.domain.pure.data.ClosedTimeRange
import javax.inject.Inject

class HistoryTrnsAct @Inject constructor(
    private val transactionRepository: TransactionRepository
) : FPAction<ClosedTimeRange, List<Transaction>>() {

    override suspend fun ClosedTimeRange.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionRepository.findAllBetween(
                startDate = from,
                endDate = to
            )
        }
    }
}
