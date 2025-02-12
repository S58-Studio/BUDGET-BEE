package com.oneSaver.allStatus.domain.action.exchange

import arrow.core.Option
import com.oneSaver.data.database.dao.read.ExchangeRatesDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.then
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeData
import com.oneSaver.allStatus.domain.pure.exchange.exchange
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
