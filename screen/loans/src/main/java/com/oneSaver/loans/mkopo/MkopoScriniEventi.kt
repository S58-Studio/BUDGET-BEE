package com.oneSaver.loans.mkopo

import com.oneSaver.loans.mkopo.data.DisplayMkopoo
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateLoanData

sealed interface MkopoScriniEventi {
    data class OnLoanCreate(val createLoanData: CreateLoanData) : MkopoScriniEventi
    data class OnReordered(val reorderedList: List<DisplayMkopoo>) : MkopoScriniEventi
    data class OnCreateAccount(val accountData: CreateAccountData) : MkopoScriniEventi
    data class OnReOrderModalShow(val show: Boolean) : MkopoScriniEventi
    data object OnAddLoan : MkopoScriniEventi
    data object OnLoanModalDismiss : MkopoScriniEventi

    /** Toggles paid off loans visibility */
    data object OnTogglePaidOffLoanVisibility : MkopoScriniEventi
}
