package com.oneSaver.data.remote

import arrow.core.Either
import com.oneSaver.data.remote.responses.ExchangeRatesResponse

interface RemoteExchangeRatesDataSource {
    suspend fun fetchEurExchangeRates(): Either<String, ExchangeRatesResponse>
}
