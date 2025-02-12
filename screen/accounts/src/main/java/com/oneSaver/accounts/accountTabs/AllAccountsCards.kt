package com.oneSaver.accounts.accountTabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.base.model.TransactionType
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.utils.drawColoredShadow
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.MediumBlack
import com.oneSaver.allStatus.userInterface.theme.MediumWhite
import com.oneSaver.allStatus.userInterface.theme.findContrastTextColor
import com.oneSaver.allStatus.userInterface.theme.isDarkColor

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Suppress("ParameterNaming")
@Composable
fun AllAccountsCards(
    hasAddButtons: Boolean,
    itemColor: Color,

    accountsHeaderCardClicked: () -> Unit = {},
    categoriesHeaderCardClicked: () -> Unit = {},
    onAddTransaction: (TransactionType) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        HeaderCard(
            title = stringRes(R.string.all_accounts),

            addButtonText = if (hasAddButtons) stringResource(R.string.add_income) else null,

            itemColor = itemColor,
            onHeaderCardClicked = { accountsHeaderCardClicked() }
        ) {
            onAddTransaction(TransactionType.INCOME)
        }

        Spacer(Modifier.width(12.dp))

        HeaderCard(
            title = stringRes(R.string.categories),

            addButtonText = if (hasAddButtons) stringResource(R.string.add_expense) else null,

            itemColor = itemColor,
            onHeaderCardClicked = { categoriesHeaderCardClicked() }
        ) {
            onAddTransaction(TransactionType.EXPENSE)
        }

        Spacer(Modifier.width(16.dp))

    }
}

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Suppress("ParameterNaming")
@Composable
fun AllAccountsCards2(
    hasAddButtons: Boolean,
    itemColor: Color,

    loansHeaderCardClicked: () -> Unit = {},
    budgetsHeaderCardClicked: () -> Unit = {},
    onAddTransaction: (TransactionType) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        HeaderCard(
            title = stringRes(R.string.loans),

            addButtonText = if (hasAddButtons) stringResource(R.string.add_income) else null,

            itemColor = itemColor,
            onHeaderCardClicked = { loansHeaderCardClicked() }
        ) {
            onAddTransaction(TransactionType.INCOME)
        }

        Spacer(Modifier.width(12.dp))

        HeaderCard(
            title = stringRes(R.string.budget),

            addButtonText = if (hasAddButtons) stringResource(R.string.add_expense) else null,

            itemColor = itemColor,
            onHeaderCardClicked = { budgetsHeaderCardClicked() }
        ) {
            onAddTransaction(TransactionType.EXPENSE)
        }

        Spacer(Modifier.width(16.dp))

    }
}
@Composable
@Suppress("ParameterNaming")
private fun RowScope.HeaderCard(
    title: String,

    addButtonText: String?,

    itemColor: Color,

    onHeaderCardClicked: () -> Unit = {},
    onAddClick: () -> Unit
) {
    val backgroundColor = if (isDarkColor(itemColor)) {
        MediumBlack.copy(alpha = 0.9f)
    } else {
        MediumWhite.copy(alpha = 0.9f)
    }

    val contrastColor = findContrastTextColor(backgroundColor)

    Column(
        modifier = Modifier
            .weight(1f)
            .drawColoredShadow(
                color = backgroundColor,
                alpha = 0.1f
            )
            .background(backgroundColor, UI.shapes.r2)
            .clickable { onHeaderCardClicked() },
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = title,
            style = UI.typo.c.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(15.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringRes(R.string.transactions),
            style = UI.typo.b2.style(
                color = contrastColor,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(Modifier.height(30.dp))

    }
}
