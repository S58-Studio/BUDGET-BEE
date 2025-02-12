package com.oneSaver.allStatus.domain.action.account

import arrow.core.nonEmptyListOf
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.allStatus.domain.pure.data.ClosedTimeRange
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import com.oneSaver.legacy.domain.pure.transaction.AccountValueFunctions
import com.oneSaver.allStatus.domain.pure.transaction.foldTransactions
import java.math.BigDecimal
import javax.inject.Inject

class CalcAccIncomeExpenseAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct
) : FPAction<CalcAccIncomeExpenseAct.Input, CalcAccIncomeExpenseAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        AccTrnsAct.Input(
            accountId = account.id.value,
            range = range
        )
    } then accTrnsAct then { accTrns ->
        foldTransactions(
            transactions = accTrns,
            arg = account.id.value,
            valueFunctions = nonEmptyListOf(
                AccountValueFunctions::income,
                AccountValueFunctions::expense,
                AccountValueFunctions::transferIncome,
                AccountValueFunctions::transferExpense
            )
        )
    } then { values ->
        Output(
            account = account,
            incomeExpensePair = IncomeExpensePair(
                income = values[0] + if (includeTransfersInCalc) values[2] else BigDecimal.ZERO,
                expense = values[1] + if (includeTransfersInCalc) values[3] else BigDecimal.ZERO
            )
        )
    }

    data class Input(
        val account: com.oneSaver.data.model.Account,
        val range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
        val includeTransfersInCalc: Boolean = false
    )

    data class Output(
        val account: com.oneSaver.data.model.Account,
        val incomeExpensePair: IncomeExpensePair
    )
}
