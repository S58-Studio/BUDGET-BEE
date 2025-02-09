package com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.financeAndMoney.design.l0_system.UI

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun mysaveIcon(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    tint: Color = UI.colors.pureInverse,
    contentDescription: String = "icon"
) {
    Icon(
        modifier = modifier,
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = tint
    )
}
