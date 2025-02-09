package com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.utils.clickableNoIndication
import com.financeAndMoney.legacy.utils.rememberInteractionSource
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BalanceRow
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.MysaveDividerLine

@Suppress("UnusedParameter")
@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ModalAmountSection(
    label: String,
    currency: String,
    amount: Double,
    modifier: Modifier = Modifier,
    Header: (@Composable () -> Unit)? = null,
    amountPaddingTop: Dp = 48.dp,
    amountPaddingBottom: Dp = 48.dp,
    showAmountModal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MysaveDividerLine()

        Header?.invoke()

        Spacer(Modifier.height(amountPaddingTop))

        Text(
            text = label,
            style = UI.typo.c.style(
                color = UI.colors.gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(4.dp))

        BalanceRow(
            modifier = Modifier
                .clickableNoIndication(rememberInteractionSource()) {
                    showAmountModal()
                }
                .testTag("amount_balance"),
            currency = currency,
            balance = amount,

            spacerCurrency = 8.dp,

            balanceFontSize = 40.sp,
            currencyFontSize = 30.sp,

            currencyUpfront = false
        )

        Spacer(Modifier.height(amountPaddingBottom))
    }
}
