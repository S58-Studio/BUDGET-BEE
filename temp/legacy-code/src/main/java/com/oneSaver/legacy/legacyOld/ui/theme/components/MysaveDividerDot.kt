package com.oneSaver.allStatus.userInterface.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.legacy.MySaveComponentPreview

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun IvyDividerDot() {
    Spacer(
        modifier = Modifier
            .size(4.dp)
            .background(UI.colors.mediumInverse, CircleShape)
    )
}

@Preview
@Composable
private fun Preview() {
    MySaveComponentPreview {
        IvyDividerDot()
    }
}
