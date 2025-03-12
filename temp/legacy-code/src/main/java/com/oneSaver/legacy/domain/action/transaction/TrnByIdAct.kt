package com.oneSaver.allStatus.domain.action.transaction

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.data.model.TransactionId
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.data.repository.mapper.TransactionMapper
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.temp.toLegacy
import java.util.UUID
import javax.inject.Inject

class TrnByIdAct @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val mapper: TransactionMapper
) : FPAction<UUID, Transaction?>() {
    override suspend fun UUID.compose(): suspend () -> Transaction? = suspend {
        this // transactionId
    } then {
        transactionRepo.findById(TransactionId(it))
    } then {
        it?.toLegacy(mapper)
    }
}
