package com.oneSaver.loans.loanDetails.events

import com.oneSaver.legacy.datamodel.LoanRecord
import com.oneSaver.loans.loans.data.DisplayLoanRecords
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateLoanRecordData
import com.oneSaver.allStatus.domain.deprecated.logic.model.EditLoanRecordData

sealed interface LoanRecordModalEvent : LoanDetailsScreenEvent {
    data class OnClickLoanRecord(val displayLoanRecords: DisplayLoanRecords) : LoanRecordModalEvent
    data class OnCreateLoanRecord(val loanRecordData: CreateLoanRecordData) :
        LoanRecordModalEvent

    data class OnDeleteLoanRecord(val loanRecord: LoanRecord) : LoanRecordModalEvent
    data class OnEditLoanRecord(val loanRecordData: EditLoanRecordData) : LoanRecordModalEvent
    data object OnDismissLoanRecord : LoanRecordModalEvent

    data object OnChangeDate : LoanRecordModalEvent
    data object OnChangeTime : LoanRecordModalEvent
}
