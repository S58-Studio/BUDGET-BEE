package com.financeAndMoney.exchangerates.data

import androidx.compose.runtime.Immutable

@Immutable
data class RatingUI(
    val from: String,
    val to: String,
    val rate: Double
)
