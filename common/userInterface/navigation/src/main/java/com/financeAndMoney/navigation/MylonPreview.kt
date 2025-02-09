package com.financeAndMoney.navigation

import androidx.compose.runtime.Composable
import com.financeAndMoney.design.system.MysaveMaterial3Theme

@Composable
fun MylonPreview(
    dark: Boolean = false,
    content: @Composable () -> Unit,
) {
    NavigationRoot(navigation = Navigation()) {
        MysaveMaterial3Theme(dark = dark, content = content)
    }
}
