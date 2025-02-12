package com.oneSaver.loans.mkopoDetails.events

sealed interface DeleteMkopoModalEvent : MkopoDetailsScreenEvent {
    data object OnDeleteMkopo : DeleteMkopoModalEvent
    data class OnDismissDeleteMkopo(val isDeleteModalVisible: Boolean) : DeleteMkopoModalEvent
}
