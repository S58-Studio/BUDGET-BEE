package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.wallet

import arrow.core.nonEmptyListOf
import arrow.core.toOption
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.action.thenMap
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.AccTrnsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.exchange.ExchangeAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.account.filterExcluded
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.ClosedTimeRange
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.IncomeExpensePair
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.ExchangeData
import com.financeAndMoney.legacy.domain.pure.transaction.AccountValueFunctions
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.foldTransactions
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.util.orZero
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
