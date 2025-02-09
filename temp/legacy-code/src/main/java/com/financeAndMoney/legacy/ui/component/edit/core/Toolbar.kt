package com.financeAndMoney.expenseAndBudgetPlanner.userInterface.edit.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.CopyAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.asBrush
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.CloseButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.DeleteButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.MysaveOutlinedButton
import java.util.UUID

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun Toolbar(
    type: TransactionType,
    initialTransactionId: UUID?,

    onDeleteTrnModal: () -> Unit,
    onChangeTransactionTypeModal: () -> Unit,

    showDuplicateButton: Boolean,
    onDuplicate: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val nav = navigation()
        CloseButton {
            nav.back()
        }

        Spacer(Modifier.weight(1f))

        when (type) {
            TransactionType.INCOME -> {
                MysaveOutlinedButton(
                    text = stringResource(R.string.income),
                    iconStart = R.drawable.ic_income
                ) {
                    onChangeTransactionTypeModal()
                }

                Spacer(Modifier.width(12.dp))
            }

            TransactionType.EXPENSE -> {
                MysaveOutlinedButton(
                    text = stringResource(R.string.expense),
                    iconStart = R.drawable.ic_expense
                ) {
                    onChangeTransactionTypeModal()
                }

                Spacer(Modifier.width(12.dp))
            }

            else -> {
                // show nothing
            }
        }

        if (initialTransactionId != null) {
            if (showDuplicateButton) {
                OutlinedIconButton(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Transparent, CircleShape)
                        .testTag("duplicate_button"),
                    shape = CircleShape,
                    colors = IconButtonDefaults.outlinedIconButtonColors()
                        .copy(contentColor = UI.colors.medium),
                    border = IconButtonDefaults.outlinedIconButtonBorder(enabled = true)
                        .copy(width = 2.dp, brush = UI.colors.medium.asBrush()),
                    onClick = onDuplicate
                ) {
                    Icon(
                        modifier = Modifier.padding(6.dp),
                        imageVector = Icons.Sharp.CopyAll,
                        contentDescription = "duplicate_button",
                        tint = UI.colors.pureInverse
                    )
                }

                Spacer(Modifier.width(12.dp))
            }

            DeleteButton(
                hasShadow = false
            ) {
                onDeleteTrnModal()
            }

            Spacer(Modifier.width(24.dp))
        }
    }
}
