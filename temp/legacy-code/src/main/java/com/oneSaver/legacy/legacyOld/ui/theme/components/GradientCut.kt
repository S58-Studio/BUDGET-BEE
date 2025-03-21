package com.oneSaver.allStatus.userInterface.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.utils.thenIf
import com.oneSaver.allStatus.userInterface.theme.Gradient
import com.oneSaver.allStatus.userInterface.theme.Transparent

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun BoxWithConstraintsScope.GradientCutBottom(
    height: Dp = 96.dp,
    alpha: Float = 1f,
    zIndex: Float? = null
) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .thenIf(zIndex != null) {
                zIndex(zIndex!!)
            }
            .background(Gradient(Transparent, UI.colors.pure).asVerticalBrush())
            .align(Alignment.BottomCenter)
            .alpha(alpha = alpha)
    )
}
