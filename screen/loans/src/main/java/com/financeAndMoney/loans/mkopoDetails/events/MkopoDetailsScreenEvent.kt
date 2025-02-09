package com.financeAndMoney.loans.mkopoDetails.events

import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateAccountData

sealed interface MkopoDetailsScreenEvent {
    data object OnEditMkopoClick : MkopoDetailsScreenEvent
    data object OnAmountClick : MkopoDetailsScreenEvent
    data object OnAddRecord : MkopoDetailsScreenEvent
    data class OnCreateAccount(val data: CreateAccountData) : MkopoDetailsScreenEvent
}
