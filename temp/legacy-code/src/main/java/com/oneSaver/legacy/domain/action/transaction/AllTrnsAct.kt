package com.oneSaver.allStatus.domain.action.transaction

import com.oneSaver.data.model.Transaction
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.frp.action.FPAction
import javax.inject.Inject

class AllTrnsAct @Inject constructor(
    private val transactionRepository: TransactionRepository
) : FPAction<Unit, List<Transaction>>() {
    override suspend fun Unit.compose(): suspend () -> List<Transaction> = suspend {
        transactionRepository.findAll()
    }
}
