package com.oneSaver.attributions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.oneSaver.userInterface.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Stable
@HiltViewModel
class AttributionsViewModel @Inject constructor(
    private val attributionsProvider: AttributionsProvider
) :
    ComposeViewModel<AttributionsState, AttributionsEvent>() {
    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState(attributionsProvider.provideAttributions())
    }

    override fun onEvent(event: AttributionsEvent) {}
}
