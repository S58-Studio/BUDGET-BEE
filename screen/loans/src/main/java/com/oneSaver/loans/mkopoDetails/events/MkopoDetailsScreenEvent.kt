package com.oneSaver.loans.mkopoDetails.events

import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData

sealed interface MkopoDetailsScreenEvent {
    data object OnEditMkopoClick : MkopoDetailsScreenEvent
    data object OnAmountClick : MkopoDetailsScreenEvent
    data object OnAddRecord : MkopoDetailsScreenEvent
    data class OnCreateAccount(val data: CreateAccountData) : MkopoDetailsScreenEvent
}
