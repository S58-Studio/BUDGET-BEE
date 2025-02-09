package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.transaction

import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.data.repository.TransactionRepository
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.toEntity
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
