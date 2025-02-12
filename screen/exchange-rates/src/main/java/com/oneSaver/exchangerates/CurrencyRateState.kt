package com.oneSaver.exchangerates

import com.oneSaver.exchangerates.data.RatingUI
import kotlinx.collections.immutable.ImmutableList

data class CurrencyRateState(
    val baseCurrency: String,
    val manual: ImmutableList<RatingUI>,
    val automatic: ImmutableList<RatingUI>
)
