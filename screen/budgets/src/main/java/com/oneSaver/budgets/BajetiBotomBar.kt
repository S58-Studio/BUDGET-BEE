package com.oneSaver.budgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Blue
import com.oneSaver.allStatus.userInterface.theme.components.BackBottomBar
import com.oneSaver.legacy.legacyOld.ui.theme.components.MysaveButton

@Composable
internal fun BoxWithConstraintsScope.BudgetBottomBar(
    onClose: () -> Unit,
    onAdd: () -> Unit
) {
    BackBottomBar(onBack = onClose) {
        MysaveButton(
            text = stringResource(R.string.add_budget),
            iconStart = R.drawable.ic_plus
        ) {
            onAdd()
        }
    }
}

@Preview
@Composable
private fun PreviewBottomBar() {
    com.oneSaver.legacy.MySavePreview {
        Column(
            Modifier
                .fillMaxSize()
                .background(Blue)
        ) {
        }

        BudgetBottomBar(
            onAdd = {},
            onClose = {}
        )
    }
}
