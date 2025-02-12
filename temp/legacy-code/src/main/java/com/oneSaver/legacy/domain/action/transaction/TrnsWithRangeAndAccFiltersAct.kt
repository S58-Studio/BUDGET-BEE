package com.oneSaver.allStatus.domain.action.transaction

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.data.database.dao.read.TransactionDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.action.thenFilter
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
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
        val range: com.oneSaver.legacy.data.model.FromToTimeRange,
        val accountIdFilterSet: Set<UUID>
    )
}
