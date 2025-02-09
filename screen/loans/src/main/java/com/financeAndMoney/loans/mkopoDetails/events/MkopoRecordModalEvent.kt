package com.financeAndMoney.loans.mkopoDetails.events

import com.financeAndMoney.legacy.datamodel.LoanRecord
import com.financeAndMoney.loans.mkopo.data.DisplayMkopoRekodi
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateLoanRecordData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.EditLoanRecordData

sealed interface MkopoRecordModalEvent : MkopoDetailsScreenEvent {
    data class OnClickMkopoRecord(val displayMkopoRekodi: DisplayMkopoRekodi) : MkopoRecordModalEvent
    data class OnCreateMkopoRecord(val loanRecordData: CreateLoanRecordData) :
        MkopoRecordModalEvent

    data class OnDeleteMkopoRecord(val loanRecord: LoanRecord) : MkopoRecordModalEvent
    data class OnEditMkopoRecord(val loanRecordData: EditLoanRecordData) : MkopoRecordModalEvent
    data object OnDismissMkopoRecord : MkopoRecordModalEvent
}
