package com.oneSaver.reportStatements

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneSaver.base.legacy.Theme
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.base.model.TransactionType
import com.oneSaver.base.utils.MySaveAdsManager
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.data.AppBaseData
import com.oneSaver.legacy.data.LegacyDueSection
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.ui.component.IncomeExpensesCards
import com.oneSaver.legacy.ui.component.transaction.TransactionsDividerLine
import com.oneSaver.legacy.ui.component.transaction.transactions
import com.oneSaver.legacy.utils.clickableNoIndication
import com.oneSaver.legacy.utils.rememberInteractionSource
import com.oneSaver.navigation.FinPieChartStatisticSkrin
//import com.oneSaver.navigation.ReportScreen
import com.oneSaver.navigation.navigation
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.GreenDark
import com.oneSaver.allStatus.userInterface.theme.GreenLight
import com.oneSaver.allStatus.userInterface.theme.IvyDark
import com.oneSaver.allStatus.userInterface.theme.Orange
import com.oneSaver.allStatus.userInterface.theme.Purple1Dark
import com.oneSaver.allStatus.userInterface.theme.Red3Light
import com.oneSaver.allStatus.userInterface.theme.components.BalanceRow
import com.oneSaver.legacy.legacyOld.ui.theme.components.CircleButtonFilled
import com.oneSaver.legacy.legacyOld.ui.theme.components.MysaveButton
import com.oneSaver.legacy.legacyOld.ui.theme.components.IvyCheckboxWithText
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import com.oneSaver.allStatus.userInterface.theme.components.MysaveOutlinedButton
import com.oneSaver.legacy.legacyOld.ui.theme.components.IvyToolbarWithoutBack
import com.oneSaver.allStatus.userInterface.theme.Blue
import com.oneSaver.allStatus.userInterface.theme.pureBlur
import com.oneSaver.userInterface.rememberScrollPositionListState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.util.UUID

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.AllInsightsTabPage(activity: Activity) {
    val viewModel: StatementsVM = viewModel()
    val state = viewModel.uiState()

    UI(
        activity = activity,
        state = state,
        onEventHandler = viewModel::onEvent
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    activity: Activity,
    state: ReportScreenState = ReportScreenState(),
    onEventHandler: (ReportScreenEvent) -> Unit = {},
) {
    val legacyTransactions = state.transactions
    val nav = navigation()
    val context = LocalContext.current
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }
    val listState = rememberScrollPositionListState(key = "reportStatements")

    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f)
                .background(pureBlur())
                .clickableNoIndication(rememberInteractionSource()) {
                    // consume clicks
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.generating_report),
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = Orange
                )
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        state = listState
    ) {
        stickyHeader {
            Toolbar(
                onExport = {
                    onEventHandler.invoke(ReportScreenEvent.OnExport(context = context))
                },
                onFilter = {
                    if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                        val adCallback = MySaveAdsManager.OnAdsCallback {
                            onEventHandler.invoke(
                                ReportScreenEvent.OnFilterOverlayVisible(
                                    filterOverlayVisible = true
                                )
                            )
                        }
                        mySaveAdsManager.displayAds(activity, adCallback)
                    }
                }
            )
        }

        item {
            Text(
                modifier = Modifier.padding(
                    start = 32.dp
                ),
                text = stringResource(R.string.reports),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(8.dp))

            BalanceRow(
                modifier = Modifier
                    .padding(start = 32.dp),
                textColor = UI.colors.pureInverse,
                currency = state.baseCurrency,
                balance = state.balance,
                balanceAmountPrefix = when {
                    state.balance > 0 -> "+"
                    else -> null
                }
            )

            Spacer(Modifier.height(20.dp))

            IncomeExpensesCards(
                history = state.history,
                currency = state.baseCurrency,
                income = state.income,
                expenses = state.expenses,
                hasAddButtons = false,
                itemColor = UI.colors.pure,
                incomeHeaderCardClicked = {
                    if (state.transactions.isNotEmpty()) {
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                nav.navigateTo(
                                    FinPieChartStatisticSkrin(
                                        type = TransactionType.INCOME,
                                        transactions = legacyTransactions.toImmutableList(),
                                        accountList = state.accountIdFilters,
                                        treatTransfersAsIncomeExpense = state.treatTransfersAsIncExp
                                    )
                                )
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)
                        }
                    }
                },
                expenseHeaderCardClicked = {
                    if (state.transactions.isNotEmpty()) {
                        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                            val adCallback = MySaveAdsManager.OnAdsCallback {
                                nav.navigateTo(
                                    FinPieChartStatisticSkrin(
                                        type = TransactionType.EXPENSE,
                                        transactions = legacyTransactions.toImmutableList(),
                                        accountList = state.accountIdFilters,
                                        treatTransfersAsIncomeExpense = state.treatTransfersAsIncExp
                                    )
                                )
                            }
                            mySaveAdsManager.displayAds(activity, adCallback)
                        }
                    }
                }
            )

            if (state.showTransfersAsIncExpCheckbox) {
                IvyCheckboxWithText(
                    modifier = Modifier
                        .padding(16.dp),
                    text = stringResource(R.string.transfers_as_income_expense),
                    checked = state.treatTransfersAsIncExp
                ) {
                    onEventHandler.invoke(
                        ReportScreenEvent.OnTreatTransfersAsIncomeExpense(
                            transfersAsIncomeExpense = it
                        )
                    )
                }
            } else {
                Spacer(Modifier.height(32.dp))
            }

            TransactionsDividerLine(
                paddingHorizontal = 0.dp
            )

            Spacer(Modifier.height(4.dp))
        }

        if (state.filter != null) {
            transactions(
                baseData = AppBaseData(
                    baseCurrency = state.baseCurrency,
                    categories = state.categories,
                    accounts = state.accounts,
                ),

                upcoming = LegacyDueSection(
                    trns = state.upcomingTransactions,
                    stats = IncomeExpensePair(
                        income = state.upcomingIncome.toBigDecimal(),
                        expense = state.upcomingExpenses.toBigDecimal()
                    ),
                    expanded = state.upcomingExpanded
                ),

                setUpcomingExpanded = {
                    onEventHandler.invoke(ReportScreenEvent.OnUpcomingExpanded(upcomingExpanded = it))
                },

                overdue = LegacyDueSection(
                    trns = state.overdueTransactions,
                    stats = IncomeExpensePair(
                        income = state.overdueIncome.toBigDecimal(),
                        expense = state.overdueExpenses.toBigDecimal()
                    ),
                    expanded = state.overdueExpanded
                ),
                setOverdueExpanded = {
                    onEventHandler.invoke(ReportScreenEvent.OnOverdueExpanded(overdueExpanded = it))
                },

                history = state.history,
                lastItemSpacer = 48.dp,

                onPayOrGet = {
                    onEventHandler.invoke(ReportScreenEvent.OnPayOrGetLegacy(transaction = it))
                },
                emptyStateTitle = stringRes(R.string.no_transactions),
                emptyStateText = stringRes(R.string.no_transactions_for_your_filter),
                onSkipTransaction = {
                    onEventHandler.invoke(ReportScreenEvent.SkipTransactionLegacy(transaction = it))
                },
                onSkipAllTransactions = {
                    onEventHandler.invoke(ReportScreenEvent.SkipTransactionsLegacy(transactions = it))
                }
            )
        } else {
            item {
                NoFilterEmptyState(
                    setFilterOverlayVisible = {
                        onEventHandler.invoke(
                            ReportScreenEvent.OnFilterOverlayVisible(
                                filterOverlayVisible = it
                            )
                        )
                    }
                )
            }
        }
    }

    FilteringOverlayy(
        visible = state.filterOverlayVisible,
        baseCurrency = state.baseCurrency,
        accounts = state.accounts,
        categories = state.categories,
        filter = state.filter,
        allTags = state.allTags,
        onClose = {
            onEventHandler.invoke(
                ReportScreenEvent.OnFilterOverlayVisible(
                    filterOverlayVisible = false
                )
            )
        },
        onSetFilter = {
            onEventHandler.invoke(ReportScreenEvent.OnFilter(filter = it))
        },
        onTagSearch = {
            onEventHandler.invoke(ReportScreenEvent.OnTagSearch(data = it))
        }
    )
}

@Composable
private fun NoFilterEmptyState(
    setFilterOverlayVisible: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        mysaveIcon(
            icon = R.drawable.ic_filter_l,
            tint = Gray
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.no_filter),
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.invalid_filter_warning),
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(32.dp))

        MysaveButton(
            iconStart = R.drawable.ic_filter_xs,
            text = stringResource(R.string.set_filter)
        ) {
            setFilterOverlayVisible(true)
        }

        Spacer(Modifier.height(96.dp))
    }
}

@Composable
private fun Toolbar(
    onExport: () -> Unit,
    onFilter: () -> Unit
) {
    IvyToolbarWithoutBack {
        // Export CSV
        MysaveOutlinedButton(
            text = stringResource(R.string.export),
            iconTint = Blue,
            textColor = Blue,
            solidBackground = true,
            padding = 8.dp,
            iconStart = R.drawable.ic_export_csv
        ) {
            onExport()
        }

        Spacer(Modifier.weight(1f))

        // Filter
        CircleButtonFilled(
            icon = R.drawable.ic_filter_xs
        ) {
            onFilter()
        }

        Spacer(Modifier.width(24.dp))
    }
}


@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview(theme: Theme = Theme.LIGHT) {
    MySavePreview(theme) {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category(
            name = NotBlankTrimmedString.unsafe("Science"),
            color = ColorInt(Purple1Dark.toArgb()),
            icon = IconAsset.unsafe("atom"),
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
        val state = ReportScreenState(
            baseCurrency = "BGN",
            balance = -6405.66,
            income = 2000.0,
            expenses = 8405.66,
            upcomingIncome = 4800.23,
            upcomingExpenses = 0.0,
            overdueIncome = 2335.12,
            overdueExpenses = 0.0,
            history =
            persistentListOf(),
            upcomingTransactions = persistentListOf(),
            overdueTransactions = persistentListOf(),

            upcomingExpanded = true,
            overdueExpanded = true,
            filter = ReportFilter.emptyFilter("BGN"),
            loading = false,
            accounts = persistentListOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = persistentListOf(
                cat1,
                Category(
                    name = NotBlankTrimmedString.unsafe("Pet"),
                    color = ColorInt(Red3Light.toArgb()),
                    icon = IconAsset.unsafe("pet"),
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                ),
                Category(
                    name = NotBlankTrimmedString.unsafe("Home"),
                    color = ColorInt(Green.toArgb()),
                    icon = null,
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                ),
            ),
        )

        UI(state = state, activity = FakeActivity())
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_NO_FILTER(theme: Theme = Theme.LIGHT) {
    MySavePreview(theme) {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category(
            name = NotBlankTrimmedString.unsafe("Science"),
            color = ColorInt(Purple1Dark.toArgb()),
            icon = IconAsset.unsafe("atom"),
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
        val state = ReportScreenState(
            baseCurrency = "BGN",
            balance = 0.0,
            income = 0.0,
            expenses = 0.0,
            upcomingIncome = 0.0,
            upcomingExpenses = 0.0,
            overdueIncome = 0.0,
            overdueExpenses = 0.0,

            history = persistentListOf(),
            upcomingTransactions = persistentListOf(),
            overdueTransactions = persistentListOf(),

            upcomingExpanded = true,
            overdueExpanded = true,

            filter = null,
            loading = false,

            accounts = persistentListOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = persistentListOf(
                cat1,
                Category(
                    name = NotBlankTrimmedString.unsafe("Pet"),
                    color = ColorInt(Red3Light.toArgb()),
                    icon = IconAsset.unsafe("pet"),
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                ),
                Category(
                    name = NotBlankTrimmedString.unsafe("Home"),
                    color = ColorInt(Green.toArgb()),
                    icon = null,
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                ),
            ),
        )

        UI(state = state, activity = FakeActivity())
    }
}
class FakeActivity : Activity()
/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportUiTest(isDark: Boolean) {
    val theme = if (isDark) Theme.DARK else Theme.LIGHT
    Preview(theme)
}

/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportNoFilterUiTest(isDark: Boolean) {
    val theme = if (isDark) Theme.DARK else Theme.LIGHT
    Preview_NO_FILTER(theme)
}