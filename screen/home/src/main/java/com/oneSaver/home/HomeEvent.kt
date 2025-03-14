package com.oneSaver.home

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.home.clientJourney.ClientJourneyCardModel
import com.oneSaver.legacy.data.model.TimePeriod

sealed interface HomeEvent {
    data class SetUpcomingExpanded(val expanded: Boolean) : HomeEvent
    data class SetOverdueExpanded(val expanded: Boolean) : HomeEvent

    data object BalanceClick : HomeEvent
    data object HiddenBalanceClick : HomeEvent
    data object HiddenIncomeClick : HomeEvent
    data class SetExpanded(val expanded: Boolean) : HomeEvent

    data object SwitchTheme : HomeEvent

    data class SetBuffer(val buffer: Double) : HomeEvent

    data class SetCurrency(val currency: String) : HomeEvent

    data class SetPeriod(val period: TimePeriod) : HomeEvent

    data class PayOrGetPlanned(val transaction: Transaction) : HomeEvent
    data class SkipPlanned(val transaction: Transaction) : HomeEvent
    data class SkipAllPlanned(val transactions: List<Transaction>) : HomeEvent

    data class DismissCustomerJourneyCard(val card: ClientJourneyCardModel) : HomeEvent

    data object SelectNextMonth : HomeEvent
    data object SelectPreviousMonth : HomeEvent
}
