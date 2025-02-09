package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.transaction

import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.data.database.dao.read.TransactionDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.action.thenFilter
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import java.util.UUID
import javax.inject.Inject

class TrnsWithRangeAndAccFiltersAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<TrnsWithRangeAndAccFiltersAct.Input, List<Transaction>>() {

    override suspend fun Input.compose(): suspend () -> List<Transaction> = suspend {
        transactionDao.findAllBetween(range.from(), range.to())
            .map { it.toLegacyDomain() }
    } thenFilter {
        accountIdFilterSet.contains(it.accountId) || accountIdFilterSet.contains(it.toAccountId)
    }

    data class Input(
        val range: com.financeAndMoney.legacy.data.model.FromToTimeRange,
        val accountIdFilterSet: Set<UUID>
    )
}
