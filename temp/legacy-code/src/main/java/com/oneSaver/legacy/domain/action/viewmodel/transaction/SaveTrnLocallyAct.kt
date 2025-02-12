package com.oneSaver.allStatus.domain.action.viewmodel.transaction

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.legacy.datamodel.toEntity
import javax.inject.Inject

class SaveTrnLocallyAct @Inject constructor(
    private val transactionRepo: TransactionRepository,
) : FPAction<Transaction, Unit>() {
    override suspend fun Transaction.compose(): suspend () -> Unit = {
        this.copy(
            isSynced = false
        ).toEntity()
    } then {
        transactionRepo::save then {}
    }
}
