package com.oneSaver.allStatus.domain.action.transaction

import arrow.core.nonEmptyListOf
import com.oneSaver.data.model.Transaction
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.allStatus.domain.action.exchange.ExchangeAct
import com.oneSaver.allStatus.domain.action.exchange.actInput
import com.oneSaver.allStatus.domain.pure.data.IncomeExpenseTransferPair
import com.oneSaver.allStatus.domain.pure.transaction.LegacyFoldTransactions
import com.oneSaver.allStatus.domain.pure.transaction.WalletValueFunctions
import com.oneSaver.allStatus.domain.pure.transaction.WalletValueFunctionsLegacy
import com.oneSaver.allStatus.domain.pure.transaction.foldTransactionsSuspend
import javax.inject.Inject

class CalcTrnsIncomeExpenseAct @Inject constructor(
    private val exchangeAct: ExchangeAct
) : FPAction<CalcTrnsIncomeExpenseAct.Input, IncomeExpenseTransferPair>() {
    override suspend fun Input.compose(): suspend () -> IncomeExpenseTransferPair = suspend {
        foldTransactionsSuspend(
            transactions = transactions,
            valueFunctions = nonEmptyListOf(
                WalletValueFunctions::income,
                WalletValueFunctions::expense,
                WalletValueFunctions::transferIncome,
                WalletValueFunctions::transferExpenses
            ),
            arg = WalletValueFunctions.Argument(
                accounts = accounts,
                baseCurrency = baseCurrency,
                exchange = ::actInput then exchangeAct
            )
        )
    } then { values ->
        IncomeExpenseTransferPair(
            income = values[0],
            expense = values[1],
            transferIncome = values[2],
            transferExpense = values[3]
        )
    }

    data class Input(
        val transactions: List<Transaction>,
        val baseCurrency: String,
        val accounts: List<Account>
    )
}

@Deprecated("Uses legacy Transaction")
class LegacyCalcTrnsIncomeExpenseAct @Inject constructor(
    private val exchangeAct: ExchangeAct
) : FPAction<LegacyCalcTrnsIncomeExpenseAct.Input, IncomeExpenseTransferPair>() {
    override suspend fun Input.compose(): suspend () -> IncomeExpenseTransferPair = suspend {
        LegacyFoldTransactions.foldTransactionsSuspend(
            transactions = transactions,
            valueFunctions = nonEmptyListOf(
                WalletValueFunctionsLegacy::income,
                WalletValueFunctionsLegacy::expense,
                WalletValueFunctionsLegacy::transferIncome,
                WalletValueFunctionsLegacy::transferExpenses
            ),
            arg = WalletValueFunctionsLegacy.Argument(
                accounts = accounts,
                baseCurrency = baseCurrency,
                exchange = ::actInput then exchangeAct
            )
        )
    } then { values ->
        IncomeExpenseTransferPair(
            income = values[0],
            expense = values[1],
            transferIncome = values[2],
            transferExpense = values[3]
        )
    }

    data class Input(
        val transactions: List<com.oneSaver.base.legacy.Transaction>,
        val baseCurrency: String,
        val accounts: List<Account>
    )
}
