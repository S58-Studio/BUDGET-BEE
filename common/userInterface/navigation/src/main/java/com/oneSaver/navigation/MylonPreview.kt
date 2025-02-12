package com.oneSaver.navigation

import androidx.compose.runtime.Composable
import com.oneSaver.design.system.MysaveMaterial3Theme

@Composable
fun MylonPreview(
    dark: Boolean = false,
    content: @Composable () -> Unit,
) {
    NavigationRoot(navigation = Navigation()) {
        MysaveMaterial3Theme(dark = dark, isTrueBlack = false, content = content)
    }
}
