package com.oneSaver.frp.view

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneSaver.frp.viewmodel.FRPViewModel
import com.oneSaver.legacy.frp.onScreenStart

@Deprecated("Legacy code. Don't use it, please.")
@Composable
inline fun <S, E, reified VM : FRPViewModel<S, E>> BoxWithConstraintsScope.FRP(
    initialEvent: E? = null,
    UI: @Composable BoxWithConstraintsScope.(
        state: S,
        onEvent: (E) -> Unit
    ) -> Unit
) {
    val viewModel: VM = viewModel()
    val state by viewModel.state().collectAsState()

    if (initialEvent != null) {
        onScreenStart {
            viewModel.onEvent(initialEvent)
        }
    }

    UI(state, viewModel::onEvent)
}
