package com.oneSaver.allStatus.domain.action.transaction

import com.oneSaver.data.model.Transaction
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.frp.action.FPAction
import com.oneSaver.allStatus.domain.pure.data.ClosedTimeRange
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
