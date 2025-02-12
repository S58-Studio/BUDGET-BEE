package com.oneSaver.allStatus.domain.action.viewmodel.home

import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.legacy.utils.beginningOfIvyTime
import com.oneSaver.allStatus.domain.pure.data.ClosedTimeRange
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import com.oneSaver.allStatus.domain.pure.transaction.isOverdue
import java.time.LocalDateTime
import javax.inject.Inject

class OverdueAct @Inject constructor(
    private val dueTrnsInfoAct: DueTrnsInfoAct
) : FPAction<OverdueAct.Input, OverdueAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        DueTrnsInfoAct.Input(
            range = ClosedTimeRange(
                from = beginningOfIvyTime(),
                to = toRange
            ),
            baseCurrency = baseCurrency,
            dueFilter = ::isOverdue
        )
    } then dueTrnsInfoAct then {
        Output(
            overdue = it.dueIncomeExpense,
            overdueTrns = it.dueTrns
        )
    }

    data class Input(
        val toRange: LocalDateTime,
        val baseCurrency: String
    )

    data class Output(
        val overdue: IncomeExpensePair,
        val overdueTrns: List<com.oneSaver.data.model.Transaction>
    )
}
