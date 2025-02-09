package com.financeAndMoney.legacy.legacyOld.ui.theme.modal

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GradientGreen
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GradientRed
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.White
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveButton
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveCircleButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.MysaveOutlinedButton

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalDynamicPrimaryAction(
    initialEmpty: Boolean,
    initialChanged: Boolean,

    testTagSave: String = "tag_save",
    testTagDelete: String = "tag_delete",

    onDelete: () -> Unit,
    dismiss: () -> Unit,
    onSave: () -> Unit
) {
    when {
        initialEmpty -> {
            ModalAdd(
                testTag = testTagSave
            ) {
                onSave()
                dismiss()
            }
        }
        else -> {
            if (!initialChanged) {
                ModalDelete(
                    testTag = testTagDelete
                ) {
                    onDelete()
                    dismiss()
                }
            } else {
                ModalSave(
                    modifier = Modifier.testTag(testTagSave)
                ) {
                    onSave()
                    dismiss()
                }
            }
        }
    }
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalSet(
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.set),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    ModalCheck(
        modifier = modifier,
        label = label,
        enabled = enabled,
        onClick = onClick
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalCheck(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    ModalPositiveButton(
        modifier = modifier,
        text = label,
        iconStart = R.drawable.ic_check,
        enabled = enabled,
        onClick = onClick
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun <T> ModalAddSave(
    item: T,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    if (item != null) {
        ModalSave(
            enabled = enabled,
            onClick = onClick
        )
    } else {
        ModalAdd(
            enabled = enabled,
            onClick = onClick
        )
    }
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalSave(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    ModalPositiveButton(
        modifier = modifier,
        text = stringResource(R.string.save),
        iconStart = R.drawable.ic_save,
        enabled = enabled,
        onClick = onClick
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalAdd(
    enabled: Boolean = true,
    testTag: String = "modal_add",
    onClick: () -> Unit
) {
    ModalPositiveButton(
        modifier = Modifier.testTag(testTag),
        text = stringResource(R.string.add),
        iconStart = R.drawable.ic_plus,
        enabled = enabled,
        onClick = onClick
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalNegativeButton(
    text: String,
    @DrawableRes iconStart: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    MysaveButton(
        text = text,
        backgroundGradient = GradientRed,
        iconStart = iconStart,
        onClick = onClick,
        enabled = enabled
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalPositiveButton(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconStart: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    MysaveButton(
        modifier = modifier,
        text = text,
        backgroundGradient = GradientGreen,
        iconStart = iconStart,
        onClick = onClick,
        enabled = enabled
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalDelete(
    enabled: Boolean = true,
    testTag: String = "modal_delete",
    onClick: () -> Unit
) {
    MysaveCircleButton(
        modifier = Modifier
            .size(40.dp)
            .testTag(testTag),
        icon = R.drawable.ic_delete,
        backgroundGradient = GradientRed,
        enabled = enabled,
        tint = White,
        onClick = onClick
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalTitle(
    text: String
) {
    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = text,
        style = UI.typo.b1.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold
        )
    )
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalSkip(
    text: String = stringResource(R.string.skip),
    onClick: () -> Unit
) {
    MysaveOutlinedButton(
        text = text,
        iconStart = null,
        solidBackground = true
    ) {
        onClick()
    }
}
