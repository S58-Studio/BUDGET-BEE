package com.financeAndMoney.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Blue
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BackBottomBar
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveButton

@Composable
internal fun BoxWithConstraintsScope.CategoriesBottomBar(
    onClose: () -> Unit,
    onAddCategory: () -> Unit
) {
    BackBottomBar(onBack = onClose) {
        MysaveButton(
            text = stringResource(R.string.add_category),
            iconStart = R.drawable.ic_plus
        ) {
            onAddCategory()
        }
    }
}

@Preview
@Composable
private fun PreviewBottomBar() {
    com.financeAndMoney.legacy.MySavePreview {
        Column(
            Modifier
                .fillMaxSize()
                .background(Blue)
        ) {
        }

        CategoriesBottomBar(
            onAddCategory = {},
            onClose = {}
        )
    }
}
