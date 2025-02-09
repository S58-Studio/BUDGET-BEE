package com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components

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
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.ActionsRow
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.CircleButton
import com.financeAndMoney.legacy.utils.navigationBarInset
import com.financeAndMoney.legacy.utils.toDensityDp
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.gradientCutBackgroundTop

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
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
