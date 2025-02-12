package com.oneSaver.userInterface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel

/**
 * A simple base ViewModel utilizing Compose' reactivity.
 */
@Stable
abstract class ComposeViewModel<UiState, UiEvent> : ViewModel() {
    /**
     * Optimized for Compose userInterface state.
     * Use only Compose primitives and immutable structures.
     * @return optimized for Compose userInterface state.
     */
    @Composable
    abstract fun uiState(): UiState

    /**
     * Sends an event of an action that happened
     * in the UI to be processed in the ViewModel.
     */
    abstract fun onEvent(event: UiEvent)
}
