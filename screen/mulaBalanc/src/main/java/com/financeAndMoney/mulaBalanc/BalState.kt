package com.financeAndMoney.mulaBalanc

import androidx.compose.runtime.Immutable
import com.financeAndMoney.legacy.data.model.TimePeriod

@Immutable
data class BalState(
    val period: TimePeriod,
    val baseCurrencyCode: String,
    val currentBalance: Double,
    val plannedPaymentsAmount: Double,
    val balanceAfterPlannedPayments: Double
)
