package com.financeAndMoney.loans.mkopo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Blue
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BackBottomBar
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveButton
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveCircleButton

@Composable
internal fun BoxWithConstraintsScope.MkopoBottomBar(
    isPaidOffLoanVisible: Boolean,
    onClose: () -> Unit,
    onAdd: () -> Unit,
    onTogglePaidOffLoanVisibility: () -> Unit
) {
    BackBottomBar(onBack = onClose) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // TODO: Add icon content description - need to update
            MysaveCircleButton(
                icon = when (isPaidOffLoanVisible) {
                    true -> R.drawable.ic_visible
                    else -> R.drawable.ic_hidden
                },
                backgroundPadding = 10.dp
            ) {
                onTogglePaidOffLoanVisibility()
            }

            Spacer(Modifier.width(12.dp))

            MysaveButton(
                text = stringResource(R.string.add_loan),
                iconStart = R.drawable.ic_plus
            ) {
                onAdd()
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBottomBar() {
    MySavePreview {
        Column(
            Modifier
                .fillMaxSize()
                .background(Blue)
        ) {
        }

        MkopoBottomBar(
            isPaidOffLoanVisible = false,
            onAdd = {},
            onClose = {},
            onTogglePaidOffLoanVisibility = {}
        )
    }
}
