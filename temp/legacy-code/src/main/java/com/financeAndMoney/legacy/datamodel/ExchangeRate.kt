package com.financeAndMoney.legacy.datamodel

import androidx.compose.runtime.Immutable
import com.financeAndMoney.data.database.entities.XchangeRateEntity

@Deprecated("Legacy data model. Will be deleted")
@Immutable
data class ExchangeRate(
    val baseCurrency: String,
    val currency: String,
    val rate: Double,
) {
    fun toEntity(): XchangeRateEntity = XchangeRateEntity(
        baseCurrency = baseCurrency,
        currency = currency,
        rate = rate
    )
}
