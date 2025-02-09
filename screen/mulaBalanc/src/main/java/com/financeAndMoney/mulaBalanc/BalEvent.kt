package com.financeAndMoney.mulaBalanc

import com.financeAndMoney.legacy.data.model.TimePeriod

sealed interface BalEvent {
    data class OnSetPeriod(val timePeriod: TimePeriod) : BalEvent
    data object OnPreviousMonth : BalEvent
    data object OnNextMonth : BalEvent
}
