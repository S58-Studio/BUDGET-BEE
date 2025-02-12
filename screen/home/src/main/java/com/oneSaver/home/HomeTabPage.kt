package com.oneSaver.home

import android.app.Activity
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.base.legacy.Theme
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.base.model.TransactionType
import com.oneSaver.base.utils.MySaveAdsManager
import com.oneSaver.frp.forward
import com.oneSaver.frp.then2
import com.oneSaver.home.clientJourney.CustomerJourney
import com.oneSaver.home.clientJourney.ClientJourneyCardModel
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.data.AppBaseData
import com.oneSaver.legacy.data.BufferInfo
import com.oneSaver.legacy.data.LegacyDueSection
import com.oneSaver.legacy.data.model.Month
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.legacy.ui.component.transaction.TransactionsDividerLine
import com.oneSaver.legacy.ui.component.transaction.transactions
import com.oneSaver.navigation.ModifyScheduledSkrin
import com.oneSaver.navigation.ModifyTransactionSkrin
import com.oneSaver.navigation.MylonPreview
import com.oneSaver.navigation.navigation
import com.oneSaver.navigation.screenScopedViewModel
import com.oneSaver.core.userInterface.R
import com.oneSaver.legacy.domain.data.MysaveCurrency
import com.oneSaver.legacy.mySaveCtx
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import com.oneSaver.legacy.legacyOld.ui.theme.modal.BufferModal
import com.oneSaver.legacy.legacyOld.ui.theme.modal.BufferModalData
import com.oneSaver.allStatus.userInterface.theme.modal.ChoosePeriodModal
import com.oneSaver.allStatus.userInterface.theme.modal.ChoosePeriodModalData
import com.oneSaver.allStatus.userInterface.theme.modal.CurrencyModal
import com.oneSaver.allStatus.userInterface.theme.modal.DeleteModal
import com.oneSaver.userInterface.rememberScrollPositionListState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.math.BigDecimal

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.HomeTabPage(activity: Activity) {
    val viewModel: HomeVM = screenScopedViewModel()
    val uiState = viewModel.uiState()

    NyumbaniUi(uiState, viewModel::onEvent, activity = activity)
}

@Suppress("LongMethod")
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.NyumbaniUi(
    uiState: NyumbaniState,
    onEvent: (NyumbaniEvent) -> Unit,
    activity: Activity,
    modifier: Modifier = Modifier,
) {
    val mysaveContext = mySaveCtx()
    val nav = navigation()

    var bufferModalData: BufferModalData? by remember { mutableStateOf(null) }
    var currencyModalVisible by remember { mutableStateOf(false) }
    var choosePeriodModal: ChoosePeriodModalData? by remember {
        mutableStateOf(null)
    }
    var moreMenuExpanded by remember { mutableStateOf(mysaveContext.moreMenuExpanded) }
    var skipAllModalVisible by remember { mutableStateOf(false) }

    val baseCurrency = uiState.baseData.baseCurrency
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }

    Column() {
        val listState = rememberScrollPositionListState(
            key = "home_lazy_column",
            initialFirstVisibleItemIndex = mysaveContext.transactionsListState
                ?.firstVisibleItemIndex ?: 0,
            initialFirstVisibleItemScrollOffset = mysaveContext.transactionsListState
                ?.firstVisibleItemScrollOffset ?: 0
        )

        NyumbaniHeader(
            expanded = uiState.expanded,
            name = uiState.name,
            period = uiState.period,
            currency = baseCurrency,
            balance = uiState.balance.toDouble(),
            hideBalance = uiState.hideBalance,

            onShowMonthModal = {
                choosePeriodModal = ChoosePeriodModalData(
                    period = uiState.period
                )
            },
            onBalanceClick = {
                if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                    val adCallback = MySaveAdsManager.OnAdsCallback {
                        onEvent(NyumbaniEvent.BalanceClick)
                    }
                    mySaveAdsManager.displayAds(activity, adCallback)
                }
            },
            onHiddenBalanceClick = {
                onEvent(NyumbaniEvent.HiddenBalanceClick)
            },
            onSelectNextMonth = {
                onEvent(NyumbaniEvent.SelectNextMonth)
            },
            onSelectPreviousMonth = {
                onEvent(NyumbaniEvent.SelectPreviousMonth)
            },
            onAddIncome = {
                if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                    Log.d("ADMOB","ON ADD INCOME CLICKED")
                    val adCallback = MySaveAdsManager.OnAdsCallback {
                        nav.navigateTo(
                            ModifyTransactionSkrin(
                                initialTransactionId = null,
                                type = TransactionType.INCOME
                            )
                        )
                    }
                    mySaveAdsManager.displayAds(activity, adCallback)
                }
            },
            onAddExpense = {
                if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                    val adCallback = MySaveAdsManager.OnAdsCallback {
                        nav.navigateTo(
                            ModifyTransactionSkrin(
                                initialTransactionId = null,
                                type = TransactionType.EXPENSE
                            )
                        )
                    }
                    mySaveAdsManager.displayAds(activity, adCallback)
                }
            },
            onAddTransfer = {
                if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                    val adCallback = MySaveAdsManager.OnAdsCallback {
                        nav.navigateTo(
                            ModifyTransactionSkrin(
                                initialTransactionId = null,
                                type = TransactionType.TRANSFER
                            )
                        )
                    }
                    mySaveAdsManager.displayAds(activity, adCallback)
                }
            },
            onAddPlannedPayment = {
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

        HomeLazyColumn(
            hideBalance = uiState.hideBalance,
            hideIncome = uiState.hideIncome,
            onSetExpand = {
                onEvent(NyumbaniEvent.SetExpanded(it))
            },
            balance = uiState.balance,

            onBalanceClick = {
                onEvent(NyumbaniEvent.BalanceClick)
            },
            onHiddenBalanceClick = {
                onEvent(NyumbaniEvent.HiddenBalanceClick)
            },
            onHiddenIncomeClick = {
                onEvent(NyumbaniEvent.HiddenIncomeClick)
            },

            period = uiState.period,
            listState = listState,

            baseData = uiState.baseData,

            upcoming = uiState.upcoming,
            overdue = uiState.overdue,

            stats = uiState.stats,
            history = uiState.history,

            customerJourneyCards = uiState.customerJourneyCards,

            onPayOrGet = forward<Transaction>() then2 {
                NyumbaniEvent.PayOrGetPlanned(it)
            } then2 onEvent,
            onDismiss = forward<ClientJourneyCardModel>() then2 {
                NyumbaniEvent.DismissCustomerJourneyCard(it)
            } then2 onEvent,
            onSkipTransaction = forward<Transaction>() then2 {
                NyumbaniEvent.SkipPlanned(it)
            } then2 onEvent,
            setUpcomingExpanded = forward<Boolean>() then2 {
                NyumbaniEvent.SetUpcomingExpanded(it)
            } then2 onEvent,
            setOverdueExpanded = forward<Boolean>() then2 {
                NyumbaniEvent.SetOverdueExpanded(it)
            } then2 onEvent,
            onSkipAllTransactions = {
                skipAllModalVisible = true
            },
            activity = activity
        )
    }


    BufferModal(
        modal = bufferModalData,
        dismiss = {
            bufferModalData = null
        },
        onBufferChanged = forward<Double>() then2 {
            NyumbaniEvent.SetBuffer(it)
        } then2 onEvent
    )

    CurrencyModal(
        title = stringResource(R.string.set_currency),
        initialCurrency = MysaveCurrency.fromCode(baseCurrency),
        visible = currencyModalVisible,
        dismiss = {
            currencyModalVisible = false
        },
        onSetCurrency = forward<String>() then2 {
            NyumbaniEvent.SetCurrency(it)
        } then2 onEvent
    )

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = {
            choosePeriodModal = null
        },
        onPeriodSelected = forward<TimePeriod>() then2 {
            NyumbaniEvent.SetPeriod(it)
        } then2 onEvent
    )

    DeleteModal(
        visible = skipAllModalVisible,
        title = stringResource(R.string.confirm_skip_all),
        description = stringResource(R.string.confirm_skip_all_description),
        dismiss = {
            skipAllModalVisible = false
        }
    ) {
        onEvent(NyumbaniEvent.SkipAllPlanned(uiState.overdue.trns))
        skipAllModalVisible = false
    }
}

@Suppress("LongParameterList")
@ExperimentalAnimationApi
@Composable
fun HomeLazyColumn(
    hideBalance: Boolean,
    hideIncome: Boolean,
    onSetExpand: (Boolean) -> Unit,
    listState: LazyListState,
    period: TimePeriod,

    baseData: AppBaseData,

    upcoming: LegacyDueSection,
    overdue: LegacyDueSection,
    balance: BigDecimal,
    stats: IncomeExpensePair,
    history: ImmutableList<TransactionHistoryItem>,

    customerJourneyCards: ImmutableList<ClientJourneyCardModel>,

    setUpcomingExpanded: (Boolean) -> Unit,
    setOverdueExpanded: (Boolean) -> Unit,

    onBalanceClick: () -> Unit,

    onPayOrGet: (Transaction) -> Unit,
    onDismiss: (ClientJourneyCardModel) -> Unit,
    onHiddenBalanceClick: () -> Unit,
    onHiddenIncomeClick: () -> Unit,
    onSkipTransaction: (Transaction) -> Unit,
    onSkipAllTransactions: (List<Transaction>) -> Unit,
    activity: Activity,
    modifier: Modifier = Modifier
) {
    val ivyContext = mySaveCtx()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                onSetExpand(listState.firstVisibleItemScrollOffset == 0)
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .testTag("home_lazy_column"),
        state = listState
    ) {
        item {
            CashFlowInfo(
                currency = baseData.baseCurrency,
                balance = balance.toDouble(),

                hideBalance = hideBalance,

                monthlyIncome = stats.income.toDouble(),
                monthlyExpenses = stats.expense.toDouble(),

                onBalanceClick = onBalanceClick,
                onHiddenBalanceClick = onHiddenBalanceClick,
                percentExpanded = 1f,
                hideIncome = hideIncome,
                onHiddenIncomeClick = onHiddenIncomeClick,
                activity = activity
            )
        }
        item {
            Spacer(Modifier.height(16.dp))

            TransactionsDividerLine()
        }

        item {
            CustomerJourney(
                customerJourneyCards = customerJourneyCards,
                onDismiss = onDismiss
            )
        }

        transactions(
            baseData = baseData,
            upcoming = upcoming,
            setUpcomingExpanded = setUpcomingExpanded,
            overdue = overdue,
            setOverdueExpanded = setOverdueExpanded,
            history = history,
            onPayOrGet = onPayOrGet,
            emptyStateTitle = stringRes(R.string.no_transactions),
            emptyStateText = stringRes(
                R.string.no_transactions_description,
                period.toDisplayLong(ivyContext.startDayOfMonth)
            ),
            onSkipTransaction = onSkipTransaction,
            onSkipAllTransactions = onSkipAllTransactions
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview(apiLevel = 33)
@Composable
private fun BoxWithConstraintsScope.PreviewHomeTab(isDark: Boolean = false) {
    MylonPreview(isDark) {
        NyumbaniUi(
            uiState = NyumbaniState(
                theme = Theme.AUTO,
                name = "",
                baseData = AppBaseData(
                    baseCurrency = "",
                    accounts = persistentListOf(),
                    categories = persistentListOf()
                ),
                balance = BigDecimal.ZERO,
                buffer = BufferInfo(
                    amount = BigDecimal.ZERO,
                    bufferDiff = BigDecimal.ZERO,
                ),
                customerJourneyCards = persistentListOf(),
                history = persistentListOf(),
                stats = IncomeExpensePair.zero(),
                upcoming = LegacyDueSection(
                    trns = persistentListOf(),
                    stats = IncomeExpensePair.zero(),
                    expanded = false,
                ),
                overdue = LegacyDueSection(
                    trns = persistentListOf(),
                    stats = IncomeExpensePair.zero(),
                    expanded = false,
                ),
                period = TimePeriod(month = Month.monthsList().first(), year = 2023),
                hideBalance = false,
                hideIncome = false,
                expanded = false
            ),
            onEvent = {},
            activity = FakeActivity()
        )
    }
}
class FakeActivity : Activity()
/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    MySavePreview(theme) {
        PreviewHomeTab(isDark)
    }
}
