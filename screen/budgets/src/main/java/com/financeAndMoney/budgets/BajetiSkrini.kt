package com.financeAndMoney.budgets

import android.app.Activity
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.utils.MySaveAdsManager
import com.financeAndMoney.budgets.model.DisplayBajeti
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.data.model.Month
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.legacy.datamodel.Budget
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.BudgetBattery
import com.financeAndMoney.legacy.utils.clickableNoIndication
import com.financeAndMoney.legacy.utils.format
import com.financeAndMoney.legacy.utils.rememberInteractionSource
import com.financeAndMoney.navigation.BajetiSkrin
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.navigation.screenScopedViewModel
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gray
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.mysaveIcon
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ReorderButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ReorderModalSingleType
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.wallet.AmountCurrencyB1
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BoxWithConstraintsScope.BudgetScreen(screen: BajetiSkrin, activity: Activity) {
    val viewModel: BajetiVM = screenScopedViewModel()
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
    state: BajetiSkriniState,
    onEvent: (BajetiSkriniEventi) -> Unit = {}
) {
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(32.dp))

        Toolbar(
            timeRange = state.timeRange,
            baseCurrency = state.baseCurrency,
            appBudgetMax = state.appBudgetMax,
            categoryBudgetsTotal = state.categoryBudgetsTotal,
            setReorderModalVisible = {
                onEvent(BajetiSkriniEventi.OnReorderModalVisible(it))
            }
        )

        Spacer(Modifier.height(8.dp))

        for (item in state.budgets) {
            Spacer(Modifier.height(24.dp))

            BudgetItem(
                displayBajeti = item,
                baseCurrency = state.baseCurrency
            ) {
                onEvent(
                    BajetiSkriniEventi.OnBudgetModalData(
                        BudgetModalData(
                            budget = item.budget,
                            baseCurrency = state.baseCurrency,
                            categories = state.categories,
                            accounts = state.accounts,
                            autoFocusKeyboard = false
                        )
                    )
                )
            }
        }

        if (state.budgets.isEmpty()) {
            Spacer(Modifier.weight(1f))

            NoBudgetsEmptyState(
                emptyStateTitle = stringResource(R.string.no_budget),
                emptyStateText = stringResource(R.string.no_budget_text)
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(150.dp)) // scroll hack
    }

    val nav = navigation()
    BudgetBottomBar(
        onAdd = {
            if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                val adCallback = MySaveAdsManager.OnAdsCallback {
                    onEvent(
                        BajetiSkriniEventi.OnBudgetModalData(
                            BudgetModalData(
                                budget = null,
                                baseCurrency = state.baseCurrency,
                                categories = state.categories,
                                accounts = state.accounts
                            )
                        )
                    )
                }
                mySaveAdsManager.displayAds(activity, adCallback)
            }
        },
        onClose = {
            nav.back()
        },
    )

    ReorderModalSingleType(
        visible = state.reorderModalVisible,
        initialItems = state.budgets,
        dismiss = {
            onEvent(BajetiSkriniEventi.OnReorderModalVisible(false))
        },
        onReordered = { onEvent(BajetiSkriniEventi.OnReorder(it)) }
    ) { _, item ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .padding(vertical = 8.dp),
            text = item.budget.name,
            style = UI.typo.b1.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )
    }

    BudgetModal(
        modal = state.budgetModalData,
        onCreate = { onEvent(BajetiSkriniEventi.OnCreateBudget(it)) },
        onEdit = { onEvent(BajetiSkriniEventi.OnEditBudget(it)) },
        onDelete = { onEvent(BajetiSkriniEventi.OnDeleteBudget(it)) },
        dismiss = {
            onEvent(BajetiSkriniEventi.OnBudgetModalData(null))
        }
    )

}

@Composable
private fun Toolbar(
    timeRange: com.financeAndMoney.legacy.data.model.FromToTimeRange?,
    baseCurrency: String,
    appBudgetMax: Double,
    categoryBudgetsTotal: Double,
    setReorderModalVisible: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, end = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.budget),
                style = UI.typo.h2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            if (timeRange != null) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = timeRange.toDisplay(),
                    style = UI.typo.b2.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            if (categoryBudgetsTotal > 0 || appBudgetMax > 0) {
                Spacer(Modifier.height(4.dp))

                val categoryBudgetText = if (categoryBudgetsTotal > 0) {
                    stringResource(
                        R.string.for_fin_categories,
                        categoryBudgetsTotal.format(baseCurrency),
                        baseCurrency
                    )
                } else {
                    ""
                }

                val appBudgetMaxText = if (appBudgetMax > 0) {
                    stringResource(
                        R.string.app_budget,
                        appBudgetMax.format(baseCurrency),
                        baseCurrency
                    )
                } else {
                    ""
                }

                val hasBothBudgetTypes =
                    categoryBudgetText.isNotBlank() && appBudgetMaxText.isNotBlank()
                Text(
                    modifier = Modifier.testTag("budgets_info_text"),
                    text = if (hasBothBudgetTypes) {
                        stringResource(
                            R.string.budget_info_both,
                            categoryBudgetText,
                            appBudgetMaxText
                        )
                    } else {
                        stringResource(R.string.budget_info, categoryBudgetText, appBudgetMaxText)
                    },
                    style = UI.typo.nC.style(
                        color = Gray,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }

        ReorderButton {
            setReorderModalVisible(true)
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun BudgetItem(
    displayBajeti: DisplayBajeti,
    baseCurrency: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoIndication(rememberInteractionSource()) {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = displayBajeti.budget.name,
                style = UI.typo.b1.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = determineBudgetType(displayBajeti.budget.parseCategoryIds().size),
                style = UI.typo.c.style(
                    color = Gray
                )
            )
        }

        AmountCurrencyB1(
            amount = displayBajeti.budget.amount,
            currency = baseCurrency,
            amountFontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.width(32.dp))
    }

    Spacer(Modifier.height(12.dp))

    BudgetBattery(
        modifier = Modifier.padding(horizontal = 16.dp),
        currency = baseCurrency,
        expenses = displayBajeti.spentAmount,
        budget = displayBajeti.budget.amount,
        backgroundNotFilled = UI.colors.medium
    ) {
        onClick()
    }
}

@Composable
private fun NoBudgetsEmptyState(
    emptyStateTitle: String,
    emptyStateText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        mysaveIcon(
            icon = R.drawable.ic_budget_xl,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = emptyStateTitle,
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = emptyStateText,
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(96.dp))
    }
}

@Preview
@Composable
private fun Preview_Empty() {
    com.financeAndMoney.legacy.MySavePreview {
        UI(
            activity = FakeActivity(),
            state = BajetiSkriniState(
                timeRange = com.financeAndMoney.legacy.data.model.TimePeriod.currentMonth(
                    startDayOfMonth = 1
                ).toRange(1), // preview
                baseCurrency = "BGN",
                categories = persistentListOf(),
                accounts = persistentListOf(),
                budgets = persistentListOf(),
                appBudgetMax = 5000.0,
                categoryBudgetsTotal = 2400.0,
                budgetModalData = null,
                reorderModalVisible = false
            )
        )
    }
}

@Preview
@Composable
private fun Preview_Budgets(theme: Theme) {
    com.financeAndMoney.legacy.MySavePreview(theme) {
        UI(
            activity = FakeActivity(),
            state = BajetiSkriniState(
                timeRange = TimePeriod(month = Month.monthsList().first()).toRange(1), // preview
                baseCurrency = "BGN",
                categories = persistentListOf(),
                accounts = persistentListOf(),
                appBudgetMax = 5000.0,
                categoryBudgetsTotal = 0.0,
                budgetModalData = null,
                reorderModalVisible = false,
                budgets = persistentListOf(
                    DisplayBajeti(
                        budget = Budget(
                            name = "Ivy Marketing",
                            amount = 1000.0,
                            accountIdsSerialized = null,
                            categoryIdsSerialized = null,
                            orderId = 0.0
                        ),
                        spentAmount = 260.0
                    ),
                    DisplayBajeti(
                        budget = Budget(
                            name = "Ivy Marketing 2",
                            amount = 1000.0,
                            accountIdsSerialized = null,
                            categoryIdsSerialized = null,
                            orderId = 0.0
                        ),
                        spentAmount = 351.0
                    ),
                    DisplayBajeti(
                        budget = Budget(
                            name = "Baldr Products, Fidgets",
                            amount = 750.0,
                            accountIdsSerialized = null,
                            categoryIdsSerialized = "cat1,cat2,cat3",
                            orderId = 0.1
                        ),
                        spentAmount = 50.0
                    )
                )
            )
        )
    }
}

/** For screenshot testing */
@Composable
fun BudgetScreenUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    Preview_Budgets(theme)
}
class FakeActivity:Activity()