package com.oneSaver.legacy.domain.action.viewmodel.home

import com.oneSaver.allStatus.domain.action.account.AccountByIdAct
import com.oneSaver.allStatus.domain.action.exchange.ExchangeAct
import com.oneSaver.allStatus.domain.action.exchange.actInput
import com.oneSaver.allStatus.domain.action.transaction.DueTrnsAct
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeTrnArgument
import com.oneSaver.allStatus.domain.pure.exchange.exchangeInBaseCurrency
import com.oneSaver.allStatus.domain.pure.transaction.expenses
import com.oneSaver.allStatus.domain.pure.transaction.incomes
import com.oneSaver.allStatus.domain.pure.transaction.sumTrns
import com.oneSaver.base.time.TimeProvider
import com.oneSaver.data.model.Transaction
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.legacy.domain.pure.data.ClosedTimeRange
import com.oneSaver.legacy.frp.lambda
import com.oneSaver.legacy.frp.then
import java.time.LocalDate
import javax.inject.Inject

class DueTrnsInfoAct @Inject constructor(
    private val dueTrnsAct: DueTrnsAct,
    private val accountByIdAct: AccountByIdAct,
    private val exchangeAct: ExchangeAct,
    private val timeProvider: TimeProvider
) : FPAction<DueTrnsInfoAct.Input, DueTrnsInfoAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output =
        suspend {
            range
        } then dueTrnsAct then { trns ->
            val dateNow = timeProvider.localDateNow()
            trns.filter {
                this.dueFilter(it, dateNow)
            }
        } then { dueTrns ->
            // We have due transactions in different currencies
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
