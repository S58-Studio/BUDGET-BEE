package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.XchangeRateEntity
import com.oneSaver.legacy.datamodel.ExchangeRate

fun XchangeRateEntity.toLegacyDomain(): ExchangeRate = ExchangeRate(
    baseCurrency = baseCurrency,
    currency = currency,
    rate = rate
)
