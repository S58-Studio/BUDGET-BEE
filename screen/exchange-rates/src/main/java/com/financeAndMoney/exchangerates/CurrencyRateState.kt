package com.financeAndMoney.exchangerates

import com.financeAndMoney.exchangerates.data.RatingUI
import kotlinx.collections.immutable.ImmutableList

data class CurrencyRateState(
    val baseCurrency: String,
    val manual: ImmutableList<RatingUI>,
    val automatic: ImmutableList<RatingUI>
)
