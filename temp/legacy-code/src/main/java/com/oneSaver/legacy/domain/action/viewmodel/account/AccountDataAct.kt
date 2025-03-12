package com.oneSaver.allStatus.domain.action.viewmodel.account

import arrow.core.toOption
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.action.thenMap
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.domain.action.account.CalcAccBalanceAct
import com.oneSaver.legacy.domain.action.account.CalcAccIncomeExpenseAct
import com.oneSaver.allStatus.domain.action.exchange.ExchangeAct
import com.oneSaver.legacy.domain.pure.data.ClosedTimeRange
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class AccountDataAct @Inject constructor(
    private val exchangeAct: ExchangeAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val calcAccIncomeExpenseAct: CalcAccIncomeExpenseAct
) : FPAction<AccountDataAct.Input, ImmutableList<com.oneSaver.legacy.data.model.AccountData>>() {

    override suspend fun Input.compose(): suspend () -> ImmutableList<com.oneSaver.legacy.data.model.AccountData> = suspend {
        accounts
    } thenMap { acc ->
        val balance = calcAccBalanceAct(
            CalcAccBalanceAct.Input(
                account = acc
            )
        ).balance

        val balanceBaseCurrency = if (acc.asset.code != baseCurrency) {
            exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency,
                        fromCurrency = acc.asset.code.toOption()
                    ),
                    amount = balance
                )
            ).orNull()
        } else {
            null
        }

        val incomeExpensePair = calcAccIncomeExpenseAct(
            CalcAccIncomeExpenseAct.Input(
                account = acc,
                range = range,
                includeTransfersInCalc = includeTransfersInCalc
            )
        ).incomeExpensePair

        com.oneSaver.legacy.data.model.AccountData(
            account = acc,
            balance = balance.toDouble(),
            balanceBaseCurrency = balanceBaseCurrency?.toDouble(),
            monthlyIncome = incomeExpensePair.income.toDouble(),
            monthlyExpenses = incomeExpensePair.expense.toDouble(),
        )
    } then {
        it.toImmutableList()
    }

    data class Input(
        val accounts: ImmutableList<com.oneSaver.data.model.Account>,
        val baseCurrency: String,
        val range: ClosedTimeRange,
        val includeTransfersInCalc: Boolean = false
    )
}
