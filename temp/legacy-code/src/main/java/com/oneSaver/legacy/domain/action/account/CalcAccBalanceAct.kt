package com.oneSaver.allStatus.domain.action.account

import arrow.core.nonEmptyListOf
import com.oneSaver.data.model.Account
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.allStatus.domain.pure.data.ClosedTimeRange
import com.oneSaver.legacy.domain.pure.transaction.AccountValueFunctions
import com.oneSaver.allStatus.domain.pure.transaction.foldTransactions
import java.math.BigDecimal
import javax.inject.Inject

class CalcAccBalanceAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct
) : FPAction<CalcAccBalanceAct.Input, CalcAccBalanceAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        AccTrnsAct.Input(
            accountId = account.id.value, range = range
        )
    } then accTrnsAct then { accTrns ->
        foldTransactions(
            transactions = accTrns,
            arg = account.id.value,
            valueFunctions = nonEmptyListOf(AccountValueFunctions::balance)
        ).head
    } then { balance ->
        Output(
            account = account, balance = balance
        )
    }

    data class Input(
        val account: Account,
        val range: ClosedTimeRange = ClosedTimeRange.allTimeIvy()
    )

    data class Output(
        val account: Account,
        val balance: BigDecimal,
    )
}
