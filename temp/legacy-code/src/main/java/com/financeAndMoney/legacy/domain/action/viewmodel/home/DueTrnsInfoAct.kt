package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home

import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.lambda
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.utils.dateNowUTC
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.AccountByIdAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.exchange.ExchangeAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.exchange.actInput
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.transaction.DueTrnsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.ClosedTimeRange
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.IncomeExpensePair
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.ExchangeTrnArgument
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.exchangeInBaseCurrency
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.expenses
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.incomes
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.sumTrns
import java.time.LocalDate
import javax.inject.Inject

class DueTrnsInfoAct @Inject constructor(
    private val dueTrnsAct: DueTrnsAct,
    private val accountByIdAct: AccountByIdAct,
    private val exchangeAct: ExchangeAct
) : FPAction<DueTrnsInfoAct.Input, DueTrnsInfoAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output =
        suspend {
            range
        } then dueTrnsAct then { trns ->
            val dateNow = dateNowUTC()
            trns.filter {
                this.dueFilter(it, dateNow)
            }
        } then { dueTrns ->
            // We have due transfers in different currencies
            val exchangeArg = ExchangeTrnArgument(
                baseCurrency = baseCurrency,
                exchange = ::actInput then exchangeAct,
                getAccount = accountByIdAct.lambda()
            )

            Output(
                dueIncomeExpense = IncomeExpensePair(
                    income = sumTrns(
                        incomes(dueTrns),
                        ::exchangeInBaseCurrency,
                        exchangeArg
                    ),
                    expense = sumTrns(
                        expenses(dueTrns),
                        ::exchangeInBaseCurrency,
                        exchangeArg
                    )
                ),
                dueTrns = dueTrns
            )
        }

    data class Input(
        val range: ClosedTimeRange,
        val baseCurrency: String,
        val dueFilter: (Transaction, LocalDate) -> Boolean
    )

    data class Output(
        val dueIncomeExpense: IncomeExpensePair,
        val dueTrns: List<Transaction>
    )
}
