package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.transaction

import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.data.model.TransactionId
import com.financeAndMoney.data.repository.TransactionRepository
import com.financeAndMoney.data.repository.mapper.TransactionMapper
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.temp.toLegacy
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
