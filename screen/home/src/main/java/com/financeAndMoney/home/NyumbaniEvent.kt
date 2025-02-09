package com.financeAndMoney.home

import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.home.clientJourney.ClientJourneyCardModel
import com.financeAndMoney.legacy.data.model.TimePeriod

sealed interface NyumbaniEvent {
    data class SetUpcomingExpanded(val expanded: Boolean) : NyumbaniEvent
    data class SetOverdueExpanded(val expanded: Boolean) : NyumbaniEvent

    data object BalanceClick : NyumbaniEvent
    data object HiddenBalanceClick : NyumbaniEvent
    data object HiddenIncomeClick : NyumbaniEvent
    data class SetExpanded(val expanded: Boolean) : NyumbaniEvent

    data object SwitchTheme : NyumbaniEvent

    data class SetBuffer(val buffer: Double) : NyumbaniEvent

    data class SetCurrency(val currency: String) : NyumbaniEvent

    data class SetPeriod(val period: TimePeriod) : NyumbaniEvent

    data class PayOrGetPlanned(val transaction: Transaction) : NyumbaniEvent
    data class SkipPlanned(val transaction: Transaction) : NyumbaniEvent
    data class SkipAllPlanned(val transactions: List<Transaction>) : NyumbaniEvent

    data class DismissCustomerJourneyCard(val card: ClientJourneyCardModel) : NyumbaniEvent

    data object SelectNextMonth : NyumbaniEvent
    data object SelectPreviousMonth : NyumbaniEvent
}
