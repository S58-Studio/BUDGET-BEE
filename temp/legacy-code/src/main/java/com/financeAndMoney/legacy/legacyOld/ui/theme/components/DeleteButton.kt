package com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveCircleButton
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GradientRed
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.White

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
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
