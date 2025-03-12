package com.oneSaver.allStatus.domain.action.wallet

import arrow.core.nonEmptyListOf
import arrow.core.toOption
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.action.thenMap
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.allStatus.domain.action.account.AccTrnsAct
import com.oneSaver.allStatus.domain.action.exchange.ExchangeAct
import com.oneSaver.allStatus.domain.pure.account.filterExcluded
import com.oneSaver.legacy.domain.pure.data.ClosedTimeRange
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeData
import com.oneSaver.legacy.domain.pure.transaction.AccountValueFunctions
import com.oneSaver.allStatus.domain.pure.transaction.foldTransactions
import com.oneSaver.allStatus.domain.pure.util.orZero
import timber.log.Timber
import javax.inject.Inject

class CalcIncomeExpenseAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct,
    private val exchangeAct: ExchangeAct
) : FPAction<CalcIncomeExpenseAct.Input, IncomeExpensePair>() {

    override suspend fun Input.compose(): suspend () -> IncomeExpensePair = suspend {
        filterExcluded(accounts)
    } thenMap { acc ->
        Pair(
            acc,
            accTrnsAct(
                AccTrnsAct.Input(
                    accountId = acc.id,
                    range = range
                )
            )
        )
    } thenMap { (acc, trns) ->
        Timber.i("acc: $acc, trns = ${trns.size}")
        Pair(
            acc,
            foldTransactions(
                transactions = trns,
                valueFunctions = nonEmptyListOf(
                    AccountValueFunctions::income,
                    AccountValueFunctions::expense
                ),
                arg = acc.id
            )
        )
    } thenMap { (acc, stats) ->
        Timber.i("acc_stats: $acc - $stats")
        stats.map {
            exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency,
                        fromCurrency = (acc.currency ?: baseCurrency).toOption()
                    ),
                    amount = it
                ),
            ).orZero()
        }
    } then { statsList ->
        IncomeExpensePair(
            income = statsList.sumOf { it[0] },
            expense = statsList.sumOf { it[1] }
        )
    }

    data class Input(
        val baseCurrency: String,
        val accounts: List<Account>,
        val range: ClosedTimeRange,
    )
}
