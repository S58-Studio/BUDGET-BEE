package com.financeAndMoney.design.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.design.MysaveContext
import com.financeAndMoney.design.api.MysaveDesign
import com.financeAndMoney.design.api.MysaveUI
import com.financeAndMoney.design.api.systems.MySaveDesign
import com.financeAndMoney.design.l0_system.UI

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun IvyComponentPreview(
    design: MysaveDesign = defaultDesign(),
    theme: Theme = Theme.LIGHT,
    content: @Composable BoxScope.() -> Unit
) {
    IvyPreview(
        design = design,
        theme = theme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.pure),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun IvyPreview(
    theme: Theme = Theme.LIGHT,
    design: MysaveDesign,
    Content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    design.context().switchTheme(theme = theme)
    MysaveUI(
        design = design,
        content = Content
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
fun defaultDesign(): MysaveDesign = object : MySaveDesign() {
    override fun context(): MysaveContext = object : MysaveContext() {
    }
}
