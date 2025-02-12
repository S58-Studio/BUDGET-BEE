package com.oneSaver.data.model

import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.PositiveDouble

data class ExchangeRate(
    val baseCurrency: AssetCode,
    val currency: AssetCode,
    val rate: PositiveDouble,
    val manualOverride: Boolean,
)
