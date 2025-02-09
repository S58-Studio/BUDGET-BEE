package com.financeAndMoney.accounts

sealed interface ACEventss {
    data class OnReorder(val reorderedList: List<com.financeAndMoney.legacy.data.model.AccountData>) :
        ACEventss
    data class OnReorderModalVisible(val reorderVisible: Boolean) : ACEventss
}
