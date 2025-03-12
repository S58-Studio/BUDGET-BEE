package com.oneSaver.loans.loans

import com.oneSaver.loans.loans.data.DisplayMkopoo
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateLoanData

sealed interface LoanScreenEvent {
    data class OnLoanCreate(val createLoanData: CreateLoanData) : LoanScreenEvent
    data class OnReordered(val reorderedList: List<DisplayMkopoo>) : LoanScreenEvent
    data class OnCreateAccount(val accountData: CreateAccountData) : LoanScreenEvent
    data class OnReOrderModalShow(val show: Boolean) : LoanScreenEvent
    data class OnTabChanged(val tab: LoanTab) : LoanScreenEvent
    data object OnAddLoan : LoanScreenEvent
    data object OnLoanModalDismiss : LoanScreenEvent
    data object OnChangeDate : LoanScreenEvent
    data object OnChangeTime : LoanScreenEvent

    /** Toggles paid off loans visibility */
    data object OnTogglePaidOffLoanVisibility : LoanScreenEvent
}
