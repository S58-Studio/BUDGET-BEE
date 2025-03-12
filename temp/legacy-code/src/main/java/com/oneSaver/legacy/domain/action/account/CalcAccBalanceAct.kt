package com.oneSaver.legacy.domain.action.account

import arrow.core.nonEmptyListOf
import com.oneSaver.allStatus.domain.action.account.AccTrnsAct
import com.oneSaver.data.model.Account
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.domain.pure.data.ClosedTimeRange
import com.oneSaver.legacy.domain.pure.transaction.AccountValueFunctions
import com.oneSaver.allStatus.domain.pure.transaction.foldTransactions
import com.oneSaver.base.time.TimeProvider
import java.math.BigDecimal
import javax.inject.Inject

class CalcAccBalanceAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct,
    private val timeProvider: TimeProvider,
) : FPAction<CalcAccBalanceAct.Input, CalcAccBalanceAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        AccTrnsAct.Input(
            accountId = account.id.value,
            range = range ?: ClosedTimeRange.allTimeIvy(timeProvider)
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

    @Suppress("DataClassDefaultValues")
    data class Input(
        val account: Account,
        val range: ClosedTimeRange? = null
    )

    data class Output(
        val account: Account,
        val balance: BigDecimal,
    )
}
