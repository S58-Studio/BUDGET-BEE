package com.oneSaver.allStatus.userInterface.theme.components

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.legacy.legacyOld.ui.theme.components.ActionsRow
import com.oneSaver.legacy.legacyOld.ui.theme.components.CircleButton
import com.oneSaver.legacy.utils.navigationBarInset
import com.oneSaver.legacy.utils.toDensityDp
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.gradientCutBackgroundTop

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun BoxWithConstraintsScope.BackBottomBar(
    bottomInset: Dp = navigationBarInset().toDensityDp(),
    onBack: () -> Unit,
    PrimaryAction: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    ActionsRow(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .gradientCutBackgroundTop(UI.colors.pure, density)
            .padding(bottom = bottomInset)
            .padding(bottom = 16.dp)
    ) {
        Spacer(Modifier.width(20.dp))

        CircleButton(
            modifier = Modifier.rotate(180f),
            icon = R.drawable.ic_arrow_right
        ) {
            onBack()
        }

        Spacer(Modifier.weight(1f))

        PrimaryAction()

        Spacer(Modifier.width(20.dp))
    }
}
