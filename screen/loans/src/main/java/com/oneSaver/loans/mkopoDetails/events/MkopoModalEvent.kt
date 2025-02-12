package com.oneSaver.loans.mkopoDetails.events

import com.oneSaver.legacy.datamodel.Loan

sealed interface MkopoModalEvent : MkopoDetailsScreenEvent {
    data object OnDismissMkopoModal : MkopoModalEvent
    data class OnEditMkopoModal(val loan: Loan, val createLoanTransaction: Boolean) :
        MkopoModalEvent

    data object PerformCalculation : MkopoModalEvent
}
