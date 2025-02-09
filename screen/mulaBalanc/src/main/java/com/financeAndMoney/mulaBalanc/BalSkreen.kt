package com.financeAndMoney.mulaBalanc

import android.app.Activity
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.utils.MySaveAdsManager
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.data.model.Month
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.legacy.utils.format
import com.financeAndMoney.navigation.BalanceSkrin
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gradient
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gray
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Orange
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.White
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BalanceRow
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.MysaveCircleButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.MysaveDividerLine
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.ChoosePeriodModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.ChoosePeriodModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.wallet.PeriodSelector

val FAB_BUTTON_SIZE = 56.dp

@Composable
fun BoxWithConstraintsScope.BalanceScreen(screen: BalanceSkrin, activity: Activity) {
    val viewModel: BalVM = viewModel()
    val uiState = viewModel.uiState()

    UI(
        activity = activity,
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    activity: Activity,
    state: BalState,
    onEvent: (BalEvent) -> Unit = {}
) {
    var choosePeriodModal: ChoosePeriodModalData? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(20.dp))

        PeriodSelector(
            period = state.period,
            onPreviousMonth = { onEvent(BalEvent.OnPreviousMonth) },
            onNextMonth = { onEvent(BalEvent.OnNextMonth) },
            onShowChoosePeriodModal = {
                choosePeriodModal = ChoosePeriodModalData(
                    period = state.period
                )
            }
        )

        Spacer(Modifier.height(32.dp))

        CurrentBalance(
            currency = state.baseCurrencyCode,
            currentBalance = state.currentBalance
        )

        Spacer(Modifier.height(32.dp))

        MysaveDividerLine(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(40.dp))

        BalanceAfterPlannedPayments(
            currency = state.baseCurrencyCode,
            currentBalance = state.currentBalance,
            plannedPaymentsAmount = state.plannedPaymentsAmount,
            balanceAfterPlannedPayments = state.balanceAfterPlannedPayments
        )

        Spacer(Modifier.weight(1f))

        CloseButton(activity = activity)

        Spacer(Modifier.height(48.dp))
    }

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = {
            choosePeriodModal = null
        }
    ) {
        onEvent(BalEvent.OnSetPeriod(it))
    }
}

@Composable
private fun ColumnScope.CurrentBalance(
    currency: String,
    currentBalance: Double
) {
    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = stringResource(R.string.your_current_balance),
        style = UI.typo.b2.style(
            color = Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(4.dp))

    BalanceRow(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        currency = currency,
        balance = currentBalance
    )
}

@Composable
private fun ColumnScope.BalanceAfterPlannedPayments(
    currency: String,
    currentBalance: Double,
    plannedPaymentsAmount: Double,
    balanceAfterPlannedPayments: Double
) {
    Text(
        modifier = Modifier
            .padding(horizontal = 32.dp),
        text = stringResource(R.string.your_balance_after_payments),
        style = UI.typo.b2.style(
            color = Orange,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(32.dp))

        BalanceRow(
            currency = currency,
            balance = balanceAfterPlannedPayments,

            balanceFontSize = 30.sp,
            currencyFontSize = 18.sp,

            currencyUpfront = false
        )

        Spacer(Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = "${currentBalance.format(2)} $currency",
                style = UI.typo.nC.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(Modifier.height(2.dp))

            val plusSign = if (plannedPaymentsAmount >= 0) "+" else ""
            Text(
                text = "${plusSign}${plannedPaymentsAmount.format(2)} $currency",
                style = UI.typo.nC.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun ColumnScope.CloseButton(activity: Activity) {
    val nav = navigation()
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }
    MysaveCircleButton(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .size(FAB_BUTTON_SIZE)
            .rotate(45f)
            .zIndex(200f),
        backgroundPadding = 8.dp,
        icon = R.drawable.ic_add,
        backgroundGradient = Gradient.solid(Gray),
        hasShadow = false,
        tint = White
    ) {
        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
            val adCallback = MySaveAdsManager.OnAdsCallback {
                nav.back()
            }
            mySaveAdsManager.displayAds(activity, adCallback)
        }
    }
}

@Preview
@Composable
private fun Preview(theme: Theme = Theme.LIGHT) {
    MySavePreview(theme) {
        UI(
            activity = FakeActivity(),
            state = BalState(
                period = TimePeriod(month = Month.monthsList().first()),
                baseCurrencyCode = "BGN",
                currentBalance = 9326.55,
                balanceAfterPlannedPayments = 8426.0,
                plannedPaymentsAmount = -900.55,
            )
        )
    }
}

/** For screenshot testing */
@Composable
fun BalanceScreenUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    Preview(theme)
}
class FakeActivity: Activity()