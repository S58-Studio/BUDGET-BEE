package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.account

import arrow.core.toOption
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.action.thenMap
import com.financeAndMoney.frp.then
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.CalcAccBalanceAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.CalcAccIncomeExpenseAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.exchange.ExchangeAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.ClosedTimeRange
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.ExchangeData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class AccountDataAct @Inject constructor(
    private val exchangeAct: ExchangeAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val calcAccIncomeExpenseAct: CalcAccIncomeExpenseAct
) : FPAction<AccountDataAct.Input, ImmutableList<com.financeAndMoney.legacy.data.model.AccountData>>() {

    override suspend fun Input.compose(): suspend () -> ImmutableList<com.financeAndMoney.legacy.data.model.AccountData> = suspend {
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

        com.financeAndMoney.legacy.data.model.AccountData(
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
        val accounts: ImmutableList<com.financeAndMoney.data.model.Account>,
        val baseCurrency: String,
        val range: ClosedTimeRange,
        val includeTransfersInCalc: Boolean = false
    )
}
