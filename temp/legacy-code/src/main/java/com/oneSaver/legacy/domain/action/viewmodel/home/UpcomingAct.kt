package com.oneSaver.allStatus.domain.action.viewmodel.home

import com.oneSaver.data.model.Transaction
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.allStatus.domain.pure.data.ClosedTimeRange
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import com.oneSaver.allStatus.domain.pure.transaction.isUpcoming
import javax.inject.Inject

class UpcomingAct @Inject constructor(
    private val dueTrnsInfoAct: DueTrnsInfoAct
) : FPAction<UpcomingAct.Input, UpcomingAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        DueTrnsInfoAct.Input(
            range = range,
            baseCurrency = baseCurrency,
            dueFilter = ::isUpcoming
        )
    } then dueTrnsInfoAct then {
        Output(
            upcoming = it.dueIncomeExpense,
            upcomingTrns = it.dueTrns
        )
    }

    data class Input(
        val range: ClosedTimeRange,
        val baseCurrency: String
    )

    data class Output(
        val upcoming: IncomeExpensePair,
        val upcomingTrns: List<Transaction>
    )
}
