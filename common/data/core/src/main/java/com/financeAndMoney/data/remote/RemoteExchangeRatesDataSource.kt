package com.financeAndMoney.data.remote

import arrow.core.Either
import com.financeAndMoney.data.remote.responses.ExchangeRatesResponse

interface RemoteExchangeRatesDataSource {
    suspend fun fetchEurExchangeRates(): Either<String, ExchangeRatesResponse>
}
