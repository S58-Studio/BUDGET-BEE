package com.financeAndMoney.transfers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.base.legacy.TransactionHistoryItem
import com.financeAndMoney.base.legacy.stringRes
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.design.utils.thenIf
import com.financeAndMoney.legacy.Constants
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.data.AppBaseData
import com.financeAndMoney.legacy.data.LegacyDueSection
import com.financeAndMoney.legacy.data.model.Month
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.mySaveCtx
import com.financeAndMoney.legacy.ui.component.IncomeExpensesCards
import com.financeAndMoney.legacy.ui.component.ItemStatisticToolbar
import com.financeAndMoney.legacy.ui.component.transaction.transactions
import com.financeAndMoney.legacy.utils.balancePrefix
import com.financeAndMoney.legacy.utils.clickableNoIndication
import com.financeAndMoney.legacy.utils.horizontalSwipeListener
import com.financeAndMoney.legacy.utils.rememberInteractionSource
import com.financeAndMoney.legacy.utils.rememberSwipeListenerState
import com.financeAndMoney.legacy.utils.setStatusBarDarkTextCompat
import com.financeAndMoney.navigation.ModifyTransactionSkrin
import com.financeAndMoney.navigation.MylonPreview
import com.financeAndMoney.navigation.FinPieChartStatisticSkrin
import com.financeAndMoney.navigation.TransactScrin
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.navigation.screenScopedViewModel
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.IncomeExpensePair
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.Gray
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GreenDark
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BalanceRow
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.BalanceRowMedium
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.ItemIconMDefaultIcon
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.dynamicContrast
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.findContrastTextColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.isDarkColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.ChoosePeriodModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.ChoosePeriodModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.DeleteConfirmationModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.DeleteModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.AccountModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.AccountModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.CategoryModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.CategoryModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.toComposeColor
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.wallet.PeriodSelector
import com.financeAndMoney.userInterface.rememberScrollPositionListState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.math.BigDecimal
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.TransferSkrin(screen: TransactScrin) {
    val viewModel: TransfersVM = screenScopedViewModel()

    val mysaveContext = mySaveCtx()
    val nav = navigation()
    val uiState = viewModel.uiState()

    val view = LocalView.current
    LaunchedEffect(Unit) {
        viewModel.start(screen)

        nav.onBackPressed[screen] = {
            setStatusBarDarkTextCompat(
                view = view,
                darkText = mysaveContext.theme == Theme.LIGHT
            )
            false
        }
    }

    UI(
        screen = screen,
        period = uiState.period,
        baseCurrency = uiState.baseCurrency,
        currency = uiState.currency,

        categories = uiState.categories,
        accounts = uiState.accounts,

        account = uiState.account,
        category = uiState.category,

        balance = uiState.balance,
        balanceBaseCurrency = uiState.balanceBaseCurrency,
        income = uiState.income,
        expenses = uiState.expenses,

        initWithTransactions = uiState.initWithTransactions,
        treatTransfersAsIncomeExpense = uiState.treatTransfersAsIncomeExpense,

        history = uiState.history,

        upcoming = uiState.upcoming,
        upcomingExpanded = uiState.upcomingExpanded,
        setUpcomingExpanded = {
            viewModel.onEvent(TransfersEvent.SetUpcomingExpanded(it))
        },
        upcomingIncome = uiState.upcomingIncome,
        upcomingExpenses = uiState.upcomingExpenses,

        overdue = uiState.overdue,
        overdueExpanded = uiState.overdueExpanded,
        setOverdueExpanded = {
            viewModel.onEvent(TransfersEvent.SetOverdueExpanded(it))
        },
        overdueIncome = uiState.overdueIncome,
        overdueExpenses = uiState.overdueExpenses,

        onSetPeriod = {
            viewModel.onEvent(
                TransfersEvent.SetPeriod(
                    screen = screen,
                    period = it
                )
            )
        },
        onNextMonth = {
            viewModel.onEvent(TransfersEvent.NextMonth(screen))
        },
        onPreviousMonth = {
            viewModel.onEvent(TransfersEvent.PreviousMonth(screen))
        },
        onDelete = {
            viewModel.onEvent(TransfersEvent.Delete(screen))
        },
        onEditCategory = {
            viewModel.onEvent(TransfersEvent.EditCategory(it))
        },
        onEditAccount = { acc, newBalance ->
            viewModel.onEvent(TransfersEvent.EditAccount(screen, acc, newBalance))
        },
        onPayOrGet = { transaction ->
            viewModel.onEvent(TransfersEvent.PayOrGet(screen, transaction))
        },
        onSkipTransaction = { transaction ->
            viewModel.onEvent(TransfersEvent.SkipTransaction(screen, transaction))
        },
        onSkipAllTransactions = { transactions ->
            viewModel.onEvent(TransfersEvent.SkipTransfers(screen, transactions))
        },
        updateAccountNameConfirmation = {
            viewModel.onEvent(TransfersEvent.UpdateAccountDeletionState(it))
        },
        enableDeletionButton = uiState.enableDeletionButton,
        skipAllModalVisible = uiState.skipAllModalVisible,
        onSkipAllModalVisible = {
            viewModel.onEvent(TransfersEvent.SetSkipAllModalVisible(it))
        },
        deleteModal1Visible = uiState.deleteModal1Visible,
        onDeleteModal1Visible = {
            viewModel.onEvent(TransfersEvent.OnDeleteModal1Visible(it))
        },
        onChoosePeriodModal = {
            viewModel.onEvent(TransfersEvent.OnChoosePeriodModalData(it))
        },
        choosePeriodModal = uiState.choosePeriodModal
    )
}

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: TransactScrin,
    period: TimePeriod,
    baseCurrency: String,
    currency: String,
    skipAllModalVisible: Boolean,
    onSkipAllModalVisible: (Boolean) -> Unit,

    account: Account?,
    category: Category?,

    updateAccountNameConfirmation: (String) -> Unit,
    enableDeletionButton: Boolean,

    categories: ImmutableList<Category>,
    accounts: ImmutableList<Account>,

    balance: Double,
    balanceBaseCurrency: Double?,
    income: Double,
    expenses: Double,
    choosePeriodModal: ChoosePeriodModalData?,

    history: ImmutableList<TransactionHistoryItem>,

    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSetPeriod: (TimePeriod) -> Unit,
    onEditAccount: (Account, Double) -> Unit,
    onEditCategory: (Category) -> Unit,
    onDelete: () -> Unit,
    deleteModal1Visible: Boolean,
    onDeleteModal1Visible: (Boolean) -> Unit,

    initWithTransactions: Boolean = false,
    treatTransfersAsIncomeExpense: Boolean = false,
    upcomingExpanded: Boolean = true,
    setUpcomingExpanded: (Boolean) -> Unit = {},
    upcomingIncome: Double = 0.0,
    upcomingExpenses: Double = 0.0,
    upcoming: ImmutableList<Transaction> = persistentListOf(),

    overdueExpanded: Boolean = true,
    setOverdueExpanded: (Boolean) -> Unit = {},
    overdueIncome: Double = 0.0,
    overdueExpenses: Double = 0.0,
    overdue: ImmutableList<Transaction> = persistentListOf(),

    onPayOrGet: (Transaction) -> Unit = {},
    onSkipTransaction: (Transaction) -> Unit = {},
    onSkipAllTransactions: (List<Transaction>) -> Unit = {},
    onChoosePeriodModal: (ChoosePeriodModalData?) -> Unit,
) {
    val mysaveContext = mySaveCtx()
    val itemColor = (account?.color ?: category?.color?.value)?.toComposeColor() ?: Gray

    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }
    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }

    val swipeListenerState = rememberSwipeListenerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(itemColor)
            .thenIf(!initWithTransactions) {
                horizontalSwipeListener(
                    sensitivity = 150,
                    state = swipeListenerState,
                    onSwipeLeft = {
                        onNextMonth()
                    },
                    onSwipeRight = {
                        onPreviousMonth()
                    }
                )
            }
    ) {
        val listState = rememberScrollPositionListState(
            key = "item_stats_lazy_column"
        )
        val density = LocalDensity.current

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 16.dp)
                .clip(UI.shapes.r1Top)
                .background(UI.colors.pure)
                .testTag("item_stats_lazy_column"),
            state = listState,
        ) {
            item {
                Header(
                    screen = screen,
                    history = history,
                    income = income,
                    expenses = expenses,
                    currency = currency,
                    baseCurrency = baseCurrency,
                    itemColor = itemColor,
                    account = account,
                    category = category,
                    balance = balance,
                    balanceBaseCurrency = balanceBaseCurrency,
                    treatTransfersAsIncomeExpense = treatTransfersAsIncomeExpense,

                    onDelete = {
                        onDeleteModal1Visible(true)
                    },
                    onEdit = {
                        when {
                            account != null -> {
                                accountModalData = AccountModalData(
                                    account = account,
                                    baseCurrency = currency,
                                    balance = balance,
                                    autoFocusKeyboard = false
                                )
                            }

                            category != null -> {
                                categoryModalData = CategoryModalData(
                                    category = category,
                                    autoFocusKeyboard = false
                                )
                            }
                        }
                    },

                    onBalanceClick = {
                        when {
                            account != null -> {
                                accountModalData = AccountModalData(
                                    account = account,
                                    baseCurrency = currency,
                                    balance = balance,
                                    adjustBalanceMode = true,
                                    autoFocusKeyboard = false
                                )
                            }
                        }
                    },
                    showCategoryModal = {
                        categoryModalData = CategoryModalData(
                            category = category,
                            autoFocusKeyboard = false
                        )
                    },
                    showAccountModal = {
                        accountModalData = AccountModalData(
                            account = account,
                            baseCurrency = currency,
                            balance = balance,
                            adjustBalanceMode = false,
                            autoFocusKeyboard = false
                        )
                    }
                )
            }

            chooseHowLongModal(
                period = period,
                itemColor = itemColor,
                initWithTransactions = initWithTransactions,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onChoosePeriodModal = onChoosePeriodModal
            )

            transactions(
                baseData = AppBaseData(
                    baseCurrency,
                    accounts,
                    categories
                ),
                upcoming = LegacyDueSection(
                    trns = upcoming,
                    stats = IncomeExpensePair(
                        income = upcomingIncome.toBigDecimal(),
                        expense = upcomingExpenses.toBigDecimal()
                    ),
                    expanded = upcomingExpanded
                ),
                setUpcomingExpanded = setUpcomingExpanded,

                overdue = LegacyDueSection(
                    trns = overdue,
                    stats = IncomeExpensePair(
                        income = overdueIncome.toBigDecimal(),
                        expense = overdueExpenses.toBigDecimal()
                    ),
                    expanded = overdueExpanded
                ),
                setOverdueExpanded = setOverdueExpanded,

                history = history,
                lastItemSpacer = with(density) {
                    (mysaveContext.screenHeight * 0.7f).toDp()
                },

                onPayOrGet = onPayOrGet,
                onSkipTransaction = onSkipTransaction,
                onSkipAllTransactions = {
                    onSkipAllModalVisible(true)
                },
                emptyStateTitle = stringRes(R.string.no_transactions),
                emptyStateText = stringRes(
                    R.string.no_transactions_for_period,
                    period.toDisplayLong(mysaveContext.startDayOfMonth)
                )
            )
        }
    }

    DeleteModals(
        account = account,
        category = category,
        updateAccountNameConfirmation = updateAccountNameConfirmation,
        enableDeletionButton = enableDeletionButton,
        onDelete = onDelete,
        skipAllModalVisible = skipAllModalVisible,
        onSkipAllModalVisible = {
            onSkipAllModalVisible(it)
        },
        onSkipAllTransactions = onSkipAllTransactions,
        deleteModal1Visible = deleteModal1Visible,
        setDeleteModal1Visible = onDeleteModal1Visible
    )

    CategoryModal(
        modal = categoryModalData,
        onCreateCategory = { },
        onEditCategory = onEditCategory,
        dismiss = {
            categoryModalData = null
        }
    )

    AccountModal(
        modal = accountModalData,
        onCreateAccount = { },
        onEditAccount = onEditAccount,
        dismiss = {
            accountModalData = null
        }
    )

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = {
            onChoosePeriodModal(null)
        }
    ) {
        onSetPeriod(it)
    }
}

private fun LazyListScope.chooseHowLongModal(
    period: TimePeriod,
    itemColor: Color,
    initWithTransactions: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onChoosePeriodModal: (ChoosePeriodModalData?) -> Unit,
) {
    item {
        // Rounded corners top effect
        Box {
            Spacer(
                Modifier
                    .height(32.dp)
                    .fillMaxWidth()
                    .background(itemColor) // itemColor is displayed below the clip
                    .background(UI.colors.pure, UI.shapes.r1Top)
            )

            PeriodSelector(
                modifier = Modifier.padding(top = 16.dp),
                period = period,
                onPreviousMonth = { if (!initWithTransactions) onPreviousMonth() },
                onNextMonth = { if (!initWithTransactions) onNextMonth() },
                onShowChoosePeriodModal = {
                    if (!initWithTransactions) {
                        onChoosePeriodModal(
                            ChoosePeriodModalData(
                                period = period
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.DeleteModals(
    deleteModal1Visible: Boolean,
    setDeleteModal1Visible: (Boolean) -> Unit,
    account: Account?,
    category: Category?,
    updateAccountNameConfirmation: (String) -> Unit,
    enableDeletionButton: Boolean,
    onDelete: () -> Unit,
    skipAllModalVisible: Boolean,
    onSkipAllModalVisible: (Boolean) -> Unit,
    onSkipAllTransactions: (List<Transaction>) -> Unit,
    overdue: ImmutableList<Transaction> = persistentListOf(),
) {
    var deleteModal3Visible by remember { mutableStateOf(false) }

    DeleteModal(
        visible = deleteModal1Visible,
        title = stringResource(R.string.deletion_confirmation),
        description = if (account != null) {
            stringResource(R.string.account_confirm_deletion_description)
        } else {
            stringResource(R.string.category_confirm_deletion_description)
        },
        dismiss = {
            setDeleteModal1Visible(false)
        }
    ) {
        deleteModal3Visible = true
    }

    DeleteConfirmationModal(
        visible = deleteModal3Visible,
        title = stringResource(id = R.string.deletion_confirmation),
        description = if (account != null) {
            stringResource(
                id = R.string.account_confirm_deletion_type_account_name,
                account.name
            )
        } else {
            stringResource(R.string.please_type_category_name, category?.name?.value ?: "")
        },
        hint = if (account != null) stringResource(id = R.string.account_name) else "Category name",
        onAccountNameChange = updateAccountNameConfirmation,
        enableDeletionButton = enableDeletionButton,
        dismiss = {
            updateAccountNameConfirmation("")
            deleteModal3Visible = false
            setDeleteModal1Visible(false)
        }
    ) {
        onDelete()
        updateAccountNameConfirmation("")
    }

    DeleteModal(
        visible = skipAllModalVisible,
        title = stringResource(R.string.confirm_skip_all),
        description = stringResource(R.string.confirm_skip_all_description),
        dismiss = {
            onSkipAllModalVisible(false)
        }
    ) {
        onSkipAllTransactions(overdue)
        onSkipAllModalVisible(false)
    }
}

@Suppress("LongParameterList")
@Composable
private fun Header(
    screen: TransactScrin,
    history: ImmutableList<TransactionHistoryItem>,
    currency: String,
    baseCurrency: String,
    itemColor: Color,
    account: Account?,
    category: Category?,
    balance: Double,
    balanceBaseCurrency: Double?,
    income: Double,
    expenses: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit,

    onBalanceClick: () -> Unit,
    showCategoryModal: () -> Unit,
    showAccountModal: () -> Unit,
    treatTransfersAsIncomeExpense: Boolean = false,
) {
    val contrastColor = findContrastTextColor(itemColor)

    val darkColor = isDarkColor(itemColor)
    setStatusBarDarkTextCompat(darkText = !darkColor)

    Column(
        modifier = Modifier.background(itemColor)
    ) {
        Spacer(Modifier.height(20.dp))

        val hideEditAndDeleteButtonForAccountTransfer =
            screen.transactions.none { it.type == TransactionType.TRANSFER }

        ItemStatisticToolbar(
            contrastColor = contrastColor,
            onEdit = onEdit,
            onDelete = onDelete,
            showEditButton = hideEditAndDeleteButtonForAccountTransfer,
            showDeleteButton = hideEditAndDeleteButtonForAccountTransfer,
        )

        Spacer(Modifier.height(24.dp))

        Item(
            contrastColor = contrastColor,
            account = account,
            category = category,
            showAccountModal = showAccountModal,
            showCategoryModal = showCategoryModal
        )

        BalanceRow(
            modifier = Modifier
                .padding(start = 32.dp)
                .testTag("mulaBalanc")
                .clickableNoIndication(rememberInteractionSource()) {
                    onBalanceClick()
                },
            textColor = contrastColor,
            currency = currency,
            balance = balance,
            balanceAmountPrefix = if (category != null) {
                balancePrefix(
                    income = income,
                    expenses = expenses
                )
            } else {
                null
            }
        )

        if (currency != baseCurrency && balanceBaseCurrency != null) {
            BalanceRowMedium(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .clickableNoIndication(rememberInteractionSource()) {
                        onBalanceClick()
                    },
                textColor = itemColor.dynamicContrast(),
                currency = baseCurrency,
                balance = balanceBaseCurrency,
                balanceAmountPrefix = if (category != null) {
                    balancePrefix(
                        income = income,
                        expenses = expenses
                    )
                } else {
                    null
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        val nav = navigation()
        IncomeExpensesCards(
            history = history,
            currency = currency,
            income = income,
            expenses = expenses,

            hasAddButtons = true,

            itemColor = itemColor,
            incomeHeaderCardClicked = {
                if (account != null) {
                    nav.navigateTo(
                        FinPieChartStatisticSkrin(
                            type = TransactionType.INCOME,
                            accountList = persistentListOf(account.id),
                            filterExcluded = false,
                            treatTransfersAsIncomeExpense = treatTransfersAsIncomeExpense
                        )
                    )
                }
            },
            expenseHeaderCardClicked = {
                if (account != null) {
                    nav.navigateTo(
                        FinPieChartStatisticSkrin(
                            type = TransactionType.EXPENSE,
                            accountList = persistentListOf(account.id),
                            filterExcluded = false,
                            treatTransfersAsIncomeExpense = treatTransfersAsIncomeExpense
                        )
                    )
                }
            }
        ) { trnType ->
            nav.navigateTo(
                ModifyTransactionSkrin(
                    initialTransactionId = null,
                    type = trnType,
                    accountId = account?.id,
                    categoryId = category?.id?.value
                )
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun Item(
    contrastColor: Color,
    account: Account?,
    category: Category?,

    showCategoryModal: () -> Unit,
    showAccountModal: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 22.dp)
            .clickableNoIndication(rememberInteractionSource()) {
                when {
                    account != null -> {
                        showAccountModal()
                    }

                    category != null -> {
                        showCategoryModal()
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            account != null -> {
                ItemIconMDefaultIcon(
                    iconName = account.icon,
                    defaultIcon = R.drawable.ic_custom_account_m,
                    tint = contrastColor
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = account.name,
                    style = UI.typo.b1.style(
                        color = contrastColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                if (!account.includeInBalance) {
                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = stringRes(R.string.excluded),
                        style = UI.typo.c.style(
                            color = account.color.toComposeColor().dynamicContrast()
                        )
                    )
                }
            }

            category != null -> {
                ItemIconMDefaultIcon(
                    iconName = category.icon?.id,
                    defaultIcon = R.drawable.ic_custom_category_m,
                    tint = contrastColor
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = category.name.value,
                    style = UI.typo.b1.style(
                        color = contrastColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            else -> {
                // Unspecified
                ItemIconMDefaultIcon(
                    iconName = null,
                    defaultIcon = R.drawable.ic_custom_category_m,
                    tint = contrastColor
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = Constants.CATEGORY_UNSPECIFIED_NAME,
                    style = UI.typo.b1.style(
                        color = contrastColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun BoxWithConstraintsScope.Preview_empty() {
    MylonPreview {
        UI(
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), // preview
            baseCurrency = "BGN",
            currency = "BGN",

            categories = persistentListOf(),
            accounts = persistentListOf(),

            balance = 1314.578,
            balanceBaseCurrency = null,
            income = 8000.0,
            expenses = 6000.0,

            history = persistentListOf(),
            category = null,
            account = Account("DSK", color = GreenDark.toArgb(), icon = "pet"),
            onSetPeriod = { },
            onPreviousMonth = {},
            onNextMonth = {},
            onDelete = {},
            onEditAccount = { _, _ -> },
            onEditCategory = {},
            updateAccountNameConfirmation = {},
            enableDeletionButton = true,
            deleteModal1Visible = false,
            onDeleteModal1Visible = {},
            skipAllModalVisible = false,
            onSkipAllModalVisible = {},
            onChoosePeriodModal = {},
            choosePeriodModal = null,
            screen = TransactScrin(),
        )
    }
}

@Preview
@Composable
private fun BoxWithConstraintsScope.Preview_crypto() {
    MylonPreview {
        UI(
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), // preview
            baseCurrency = "BGN",
            currency = "ADA",

            categories = persistentListOf(),
            accounts = persistentListOf(),

            balance = 1314.578,
            balanceBaseCurrency = 2879.28,
            income = 8000.0,
            expenses = 6000.0,

            history = persistentListOf(),
            category = null,
            account = Account(
                name = "DSK",
                color = GreenDark.toArgb(),
                icon = "pet",
                includeInBalance = false
            ),
            onSetPeriod = { },
            onPreviousMonth = {},
            onNextMonth = {},
            onDelete = {},
            onEditAccount = { _, _ -> },
            onEditCategory = {},
            updateAccountNameConfirmation = {},
            enableDeletionButton = true,
            deleteModal1Visible = false,
            onDeleteModal1Visible = {},
            skipAllModalVisible = false,
            onSkipAllModalVisible = {},
            onChoosePeriodModal = {},
            choosePeriodModal = null,
            screen = TransactScrin(),
        )
    }
}

@Preview
@Composable
private fun BoxWithConstraintsScope.Preview_empty_upcoming() {
    MylonPreview {
        UI(
            period = TimePeriod(month = Month.monthsList().first(), year = 2023),
            baseCurrency = "BGN",
            currency = "BGN",

            categories = persistentListOf(),
            accounts = persistentListOf(),

            balance = 1314.578,
            balanceBaseCurrency = null,
            income = 8000.0,
            expenses = 6000.0,

            history = persistentListOf(),
            category = null,
            account = Account("DSK", color = GreenDark.toArgb(), icon = "pet"),
            onSetPeriod = { },
            onPreviousMonth = {},
            onNextMonth = {},
            onDelete = {},
            onEditAccount = { _, _ -> },
            onEditCategory = {},
            upcoming = persistentListOf(
                Transaction(
                    UUID(1L, 2L),
                    TransactionType.EXPENSE,
                    BigDecimal.valueOf(10L)
                )
            ),
            updateAccountNameConfirmation = {},
            enableDeletionButton = true,
            deleteModal1Visible = false,
            onDeleteModal1Visible = {},
            skipAllModalVisible = false,
            onSkipAllModalVisible = {},
            onChoosePeriodModal = {},
            choosePeriodModal = null,
            screen = TransactScrin(),
        )
    }
}

/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionsUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    MySavePreview(theme) {
        Preview_empty_upcoming()
    }
}