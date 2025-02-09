package com.financeAndMoney.iliyopangwa.list

import android.app.Activity
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.base.utils.MySaveAdsManager
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.data.model.CategoryId
import com.financeAndMoney.data.model.IntervalType
import com.financeAndMoney.data.model.primitive.ColorInt
import com.financeAndMoney.data.model.primitive.NotBlankTrimmedString
import com.financeAndMoney.design.l0_system.Purple
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.PlannedPaymentRule
import com.financeAndMoney.legacy.utils.timeNowUTC
import com.financeAndMoney.navigation.ModifyScheduledSkrin
import com.financeAndMoney.navigation.ScheduledPaymntsSkrin
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.navigation.screenScopedViewModel
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.userInterface.rememberScrollPositionListState
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Green
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Orange
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.ScheduledPaymntsSkrin(screen: ScheduledPaymntsSkrin, activity: Activity) {
    val viewModel: ScheduledPaymntsKard = screenScopedViewModel()
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
    state: ScheduledPaymntSkrinState,
    onEvent: (ScheduledPaymntsSkrinEvent) -> Unit = {}
) {
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }
    PlannedPaymentsLazyColumn(
        Header = {
            Spacer(Modifier.height(32.dp))

            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = stringResource(R.string.planned_payments_inline),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )

            Spacer(Modifier.height(24.dp))
        },
        currency = state.currency,
        categories = state.categories,
        accounts = state.accounts,
        oneTime = state.oneTimePlannedPayment,
        oneTimeIncome = state.oneTimeIncome,
        oneTimeExpenses = state.oneTimeExpenses,
        recurring = state.recurringPlannedPayment,
        recurringIncome = state.recurringIncome,
        recurringExpenses = state.recurringExpenses,
        oneTimeExpanded = state.isOneTimePaymentsExpanded,
        recurringExpanded = state.isRecurringPaymentsExpanded,
        setOneTimeExpanded = {
            onEvent(ScheduledPaymntsSkrinEvent.OnOneTimePaymentsExpanded(it))
        },
        setRecurringExpanded = {
            onEvent(ScheduledPaymntsSkrinEvent.OnRecurringPaymentsExpanded(it))
        },
        listState = rememberScrollPositionListState(key = "plannedPayments")
    )

    val nav = navigation()
    PlannedPaymentsBottomBar(
        onClose = {
            if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                val adCallback = MySaveAdsManager.OnAdsCallback {
                    nav.back()
                }
                mySaveAdsManager.displayAds(activity, adCallback)
            }
        },
        onAdd = {
            if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                val adCallback = MySaveAdsManager.OnAdsCallback {
                    nav.navigateTo(
                        ModifyScheduledSkrin(
                            type = TransactionType.EXPENSE,
                            plannedPaymentRuleId = null
                        )
                    )
                }
                mySaveAdsManager.displayAds(activity, adCallback)
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    MySavePreview {
        val account = Account(name = "Cash", Green.toArgb())
        val food = Category(
            name = NotBlankTrimmedString.unsafe("Food"),
            color = ColorInt(Purple.toArgb()),
            icon = null,
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
        val shisha = Category(
            name = NotBlankTrimmedString.unsafe("Shisha"),
            color = ColorInt(Orange.toArgb()),
            icon = null,
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )

        UI(
            activity = FakeActivity(),
            ScheduledPaymntSkrinState(
                currency = "BGN",
                accounts = persistentListOf(account),
                categories = persistentListOf(food, shisha),
                oneTimePlannedPayment = persistentListOf(
                    PlannedPaymentRule(
                        accountId = account.id,
                        title = "Lidl pazar",
                        categoryId = food.id.value,
                        amount = 250.75,
                        startDate = timeNowUTC().plusDays(5),
                        oneTime = true,
                        intervalType = null,
                        intervalN = null,
                        type = TransactionType.EXPENSE
                    )
                ),
                oneTimeExpenses = 250.75,
                oneTimeIncome = 0.0,
                recurringPlannedPayment = persistentListOf(
                    PlannedPaymentRule(
                        accountId = account.id,
                        title = "Tabu",
                        categoryId = shisha.id.value,
                        amount = 1025.5,
                        startDate = timeNowUTC().plusDays(5),
                        oneTime = false,
                        intervalType = IntervalType.MONTH,
                        intervalN = 1,
                        type = TransactionType.EXPENSE
                    )
                ),
                recurringExpenses = 1025.5,
                recurringIncome = 0.0,
                isOneTimePaymentsExpanded = true,
                isRecurringPaymentsExpanded = true
            )
        )
    }
}
class FakeActivity:Activity()