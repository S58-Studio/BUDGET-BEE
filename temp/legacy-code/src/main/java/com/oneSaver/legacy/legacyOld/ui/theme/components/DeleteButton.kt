package com.oneSaver.allStatus.userInterface.theme.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.oneSaver.legacy.legacyOld.ui.theme.components.MysaveCircleButton
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.GradientRed
import com.oneSaver.allStatus.userInterface.theme.White

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun DeleteButton(
    modifier: Modifier = Modifier,
    hasShadow: Boolean = true,
    onClick: () -> Unit,
) {
    MysaveCircleButton(
        modifier = modifier
            .size(48.dp)
            .testTag("delete_button"),
        backgroundPadding = 6.dp,
        icon = R.drawable.ic_delete,
        backgroundGradient = GradientRed,
        enabled = true,
        hasShadow = hasShadow,
        tint = White,
        onClick = onClick
    )
}
