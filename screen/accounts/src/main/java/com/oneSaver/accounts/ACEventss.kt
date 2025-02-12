package com.oneSaver.accounts

sealed interface ACEventss {
    data class OnReorder(val reorderedList: List<com.oneSaver.legacy.data.model.AccountData>) :
        ACEventss
    data class OnReorderModalVisible(val reorderVisible: Boolean) : ACEventss
}
