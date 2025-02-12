package com.oneSaver.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.oneSaver.data.database.entities.XchangeRateEntity
import com.oneSaver.data.model.ExchangeRate
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.PositiveDouble
import com.oneSaver.data.remote.responses.ExchangeRatesResponse
import javax.inject.Inject

class ExchangeRateMapper @Inject constructor() {
    fun XchangeRateEntity.toDomain(): Either<String, ExchangeRate> = either {
        ExchangeRate(
            baseCurrency = AssetCode.from(baseCurrency).bind(),
            currency = AssetCode.from(currency).bind(),
            rate = PositiveDouble.from(rate).bind(),
            manualOverride = manualOverride,
        )
    }

    fun ExchangeRate.toEntity(): XchangeRateEntity {
        return XchangeRateEntity(
            baseCurrency = baseCurrency.code,
            currency = currency.code,
            rate = rate.value,
            manualOverride = manualOverride,
        )
    }

    fun ExchangeRatesResponse.toDomain(): Either<String, List<ExchangeRate>> = either {
        val domainRates = rates.mapNotNull { (currency, rate) ->
            either {
                ExchangeRate(
                    baseCurrency = AssetCode.EUR,
                    currency = AssetCode.from(currency).bind(),
                    rate = PositiveDouble.from(rate).bind(),
                    manualOverride = false
                )
            }.getOrNull()
        }
        ensure(domainRates.isNotEmpty()) { "Failed to map exchange rates to domain" }
        domainRates
    }
}
