package com.oneSaver.mulaBalanc

import androidx.compose.runtime.Immutable
import com.oneSaver.legacy.data.model.TimePeriod

@Immutable
data class BalState(
    val period: TimePeriod,
    val baseCurrencyCode: String,
    val currentBalance: Double,
    val plannedPaymentsAmount: Double,
    val balanceAfterPlannedPayments: Double
)
