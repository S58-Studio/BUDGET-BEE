package com.financeAndMoney.disclaimer

import kotlinx.collections.immutable.ImmutableList

data class KanushoViewState(
    val checkboxes: ImmutableList<CheckboxViewState>,
    val agreeButtonEnabled: Boolean,
)

data class CheckboxViewState(
    val text: String,
    val checked: Boolean
)

sealed interface KanushoViewEvent {
    data class OnCheckboxClick(val index: Int) : KanushoViewEvent
    data object OnAgreeClick : KanushoViewEvent
}