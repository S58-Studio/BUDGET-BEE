package com.financeAndMoney.legacy.datamodel.temp

import com.financeAndMoney.data.database.entities.XchangeRateEntity
import com.financeAndMoney.legacy.datamodel.ExchangeRate

fun XchangeRateEntity.toLegacyDomain(): ExchangeRate = ExchangeRate(
    baseCurrency = baseCurrency,
    currency = currency,
    rate = rate
)
