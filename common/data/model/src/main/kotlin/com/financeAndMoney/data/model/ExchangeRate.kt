package com.financeAndMoney.data.model

import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.model.primitive.PositiveDouble

data class ExchangeRate(
    val baseCurrency: AssetCode,
    val currency: AssetCode,
    val rate: PositiveDouble,
    val manualOverride: Boolean,
)
