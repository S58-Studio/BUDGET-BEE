package com.financeAndMoney.legacy.legacyOld.ui.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.legacy.MySaveComponentPreview
import com.financeAndMoney.legacy.utils.drawColoredShadow
import com.financeAndMoney.design.utils.thenIf
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gradient
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GradientMysave
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GradientRed
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.White
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.mysaveIcon

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun MysaveCircleButton(
    modifier: Modifier = Modifier,
    backgroundPadding: Dp = 0.dp,
    backgroundGradient: Gradient = GradientMysave,
    horizontalGradient: Boolean = true,
    @DrawableRes icon: Int,
    tint: Color = White,
    enabled: Boolean = true,
    hasShadow: Boolean = true,
    onClick: () -> Unit
) {
    mysaveIcon(
        modifier = modifier
            .thenIf(enabled && hasShadow) {
                drawColoredShadow(
                    color = backgroundGradient.startColor,
                    borderRadius = 0.dp,
                    shadowRadius = 16.dp,
                    offsetX = 0.dp,
                    offsetY = 8.dp
                )
            }
            .clip(UI.shapes.rFull)
            .background(
                brush = if (enabled) {
                    if (horizontalGradient) {
                        backgroundGradient.asHorizontalBrush()
                    } else {
                        backgroundGradient.asVerticalBrush()
                    }
                } else {
                    SolidColor(UI.colors.gray)
                },
                shape = UI.shapes.rFull
            )
            .clickable(onClick = onClick, enabled = enabled)
            .padding(all = backgroundPadding),
        icon = icon,
        tint = tint,
        contentDescription = "circle button"
    )
}

@Composable
fun IvyRecButton(
    modifier: Modifier = Modifier,
    backgroundPadding: Dp = 0.dp,
    backgroundGradient: Gradient = GradientMysave,
    horizontalGradient: Boolean = true,
    @DrawableRes icon: Int,
    tint: Color = White,
    enabled: Boolean = true,
    hasShadow: Boolean = true,
    onClick: () -> Unit
) {
    mysaveIcon(
        modifier = modifier
            .thenIf(enabled && hasShadow) {
                drawColoredShadow(
                    color = backgroundGradient.startColor,
                    borderRadius = 0.dp,
                    shadowRadius = 16.dp,
                    offsetX = 0.dp,
                    offsetY = 8.dp
                )
            }
            .clip(UI.shapes.rFullRec)
            .background(
                brush = if (enabled) {
                    if (horizontalGradient) {
                        backgroundGradient.asHorizontalBrush()
                    } else {
                        backgroundGradient.asVerticalBrush()
                    }
                } else {
                    SolidColor(UI.colors.gray)
                },
                shape = UI.shapes.rFullRec
            )
            .clickable(onClick = onClick, enabled = enabled)
            .padding(all = backgroundPadding),
        icon = icon,
        tint = tint,
        contentDescription = "circle button"
    )
}

@Preview
@Composable
private fun PreviewIvyCircleButton_Enabled() {
    MySaveComponentPreview {
        MysaveCircleButton(
            icon = R.drawable.ic_delete,
            backgroundGradient = GradientRed,
            tint = White
        ) {
        }
    }
}

@Preview
@Composable
private fun PreviewIvyCircleButton_Disabled() {
    MySaveComponentPreview {
        MysaveCircleButton(
            icon = R.drawable.ic_delete,
            backgroundGradient = GradientRed,
            enabled = false,
            tint = White
        ) {
        }
    }
}
