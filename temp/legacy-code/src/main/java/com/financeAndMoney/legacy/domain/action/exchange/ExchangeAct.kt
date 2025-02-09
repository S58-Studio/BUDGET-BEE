package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.exchange

import arrow.core.Option
import com.financeAndMoney.data.database.dao.read.ExchangeRatesDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.ExchangeData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.exchange
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
) : FPAction<ExchangeAct.Input, Option<BigDecimal>>() {
    override suspend fun Input.compose(): suspend () -> Option<BigDecimal> = suspend {
        exchange(
            data = data,
            amount = amount,
            getExchangeRate = exchangeRatesDao::findByBaseCurrencyAndCurrency then {
                it?.toLegacyDomain()
            }
        )
    }

    data class Input(
        val data: ExchangeData,
        val amount: BigDecimal
    )
}

fun actInput(
    data: ExchangeData,
    amount: BigDecimal
): ExchangeAct.Input = ExchangeAct.Input(
    data = data,
    amount = amount
)
