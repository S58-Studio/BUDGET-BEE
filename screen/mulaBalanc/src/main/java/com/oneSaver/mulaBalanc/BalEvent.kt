package com.oneSaver.mulaBalanc

import com.oneSaver.legacy.data.model.TimePeriod

sealed interface BalEvent {
    data class OnSetPeriod(val timePeriod: TimePeriod) : BalEvent
    data object OnPreviousMonth : BalEvent
    data object OnNextMonth : BalEvent
}
