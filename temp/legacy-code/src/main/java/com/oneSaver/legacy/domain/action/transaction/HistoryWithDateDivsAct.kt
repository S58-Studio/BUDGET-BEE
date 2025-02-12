package com.oneSaver.allStatus.domain.action.transaction

import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.allStatus.domain.pure.data.ClosedTimeRange
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class HistoryWithDateDivsAct @Inject constructor(
    private val historyTrnsAct: HistoryTrnsAct,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct
) : FPAction<HistoryWithDateDivsAct.Input, ImmutableList<TransactionHistoryItem>>() {

    override suspend fun Input.compose(): suspend () -> ImmutableList<TransactionHistoryItem> =
        suspend {
            range
        } then historyTrnsAct then { trns ->
            TrnsWithDateDivsAct.Input(
                baseCurrency = baseCurrency,
                transactions = trns
            )
        } then trnsWithDateDivsAct then { it.toImmutableList() }

    data class Input(
        val range: ClosedTimeRange,
        val baseCurrency: String
    )
}
