package com.oneSaver.iliyopangwa.list

sealed interface ScheduledPaymntsSkrinEvent {
    data class OnOneTimePaymentsExpanded(val isExpanded: Boolean) : ScheduledPaymntsSkrinEvent
    data class OnRecurringPaymentsExpanded(val isExpanded: Boolean) : ScheduledPaymntsSkrinEvent
}
