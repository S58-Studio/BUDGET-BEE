package com.oneSaver.data.model

import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.NonZeroDouble
import com.oneSaver.data.model.primitive.PositiveDouble

/**
 * Represents monetary value. (like 10 USD, 5 EUR, 0.005 BTC, 12 GOLD_GRAM)
 */
data class PositiveValue(
    val amount: PositiveDouble,
    val asset: AssetCode,
)

data class Value(
    val amount: NonZeroDouble,
    val asset: AssetCode,
)