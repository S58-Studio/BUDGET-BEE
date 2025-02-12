package com.oneSaver.transfers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import arrow.core.toOption
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.data.database.dao.write.WriteCategoryDao
import com.oneSaver.data.database.dao.write.WritePlannedPaymentRuleDao
import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.repository.AccountRepository
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.data.repository.TagRepository
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.data.repository.mapper.TransactionMapper
import com.oneSaver.design.l0_system.RedLight
import com.oneSaver.frp.then
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.legacy.data.model.toCloseTimeRange
import com.oneSaver.legacy.datamodel.temp.toImmutableLegacyTags
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.legacy.domain.deprecated.logic.AccountCreator
import com.oneSaver.legacy.utils.computationThread
import com.oneSaver.legacy.utils.dateNowUTC
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.legacy.utils.isNotNullOrBlank
import com.oneSaver.legacy.utils.selectEndTextFieldValue
import com.oneSaver.navigation.Navigation
import com.oneSaver.navigation.TransactScrin
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.domain.action.account.AccTrnsAct
import com.oneSaver.allStatus.domain.action.account.AccountsAct
import com.oneSaver.allStatus.domain.action.account.CalcAccBalanceAct
import com.oneSaver.allStatus.domain.action.account.CalcAccIncomeExpenseAct
import com.oneSaver.allStatus.domain.action.exchange.ExchangeAct
import com.oneSaver.allStatus.domain.action.settings.BaseCurrencyAct
import com.oneSaver.allStatus.domain.action.transaction.LegacyCalcTrnsIncomeExpenseAct
import com.oneSaver.allStatus.domain.action.transaction.LegacyTrnsWithDateDivsAct
import com.oneSaver.allStatus.domain.deprecated.logic.CategoryCreator
import com.oneSaver.allStatus.domain.deprecated.logic.PlannedPaymentsLogic
import com.oneSaver.allStatus.domain.deprecated.logic.WalletAccountLogic
import com.oneSaver.allStatus.domain.deprecated.logic.WalletCategoryLogic
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeData
import com.oneSaver.allStatus.userInterface.theme.modal.ChoosePeriodModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.oneSaver.legacy.datamodel.Account as LegacyAccount

@Stable
@HiltViewModel
class TransfersVM @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountDao: AccountDao,
    private val categoryRepository: CategoryRepository,
    private val mysaveContext: MySaveCtx,
    private val nav: Navigation,
    private val accountLogic: WalletAccountLogic,
    private val categoryLogic: WalletCategoryLogic,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val sharedPrefs: SharedPrefs,
    private val accountsAct: AccountsAct,
    private val accTrnsAct: AccTrnsAct,
    private val trnsWithDateDivsAct: LegacyTrnsWithDateDivsAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val calcAccIncomeExpenseAct: CalcAccIncomeExpenseAct,
    private val calcTrnsIncomeExpenseAct: LegacyCalcTrnsIncomeExpenseAct,
    private val exchangeAct: ExchangeAct,
    private val transactionRepository: TransactionRepository,
    private val categoryWriter: WriteCategoryDao,
    private val plannedPaymentRuleWriter: WritePlannedPaymentRuleDao,
    private val transactionMapper: TransactionMapper,
    private val tagRepository: TagRepository,
) : ComposeViewModel<TransferState, TransfersEvent>() {

    private val period = mutableStateOf(mysaveContext.selectedPeriod)
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<LegacyAccount>>(persistentListOf())
    private val baseCurrency = mutableStateOf("")
    private val currency = mutableStateOf("")
    private val balance = mutableDoubleStateOf(0.0)
    private val balanceBaseCurrency = mutableStateOf<Double?>(null)
    private val income = mutableDoubleStateOf(0.0)
    private val expenses = mutableDoubleStateOf(0.0)

    // Upcoming
    private val upcoming = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val upcomingIncome = mutableDoubleStateOf(0.0)
    private val upcomingExpenses = mutableDoubleStateOf(0.0)
    private val upcomingExpanded = mutableStateOf(false)

    // Overdue
    private val overdue = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val overdueIncome = mutableDoubleStateOf(0.0)
    private val overdueExpenses = mutableDoubleStateOf(0.0)
    private val overdueExpanded = mutableStateOf(true)

    // History
    private val history =
        mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())

    private val account = mutableStateOf<LegacyAccount?>(null)
    private val category = mutableStateOf<Category?>(null)
    private val initWithTransactions = mutableStateOf(false)
    private val treatTransfersAsIncomeExpense = mutableStateOf(false)
    private val accountNameConfirmation = mutableStateOf(selectEndTextFieldValue(""))
    private val enableDeletionButton = mutableStateOf(false)
    private val skipAllModalVisible = mutableStateOf(false)
    private val deleteModal1Visible = mutableStateOf(false)
    private val choosePeriodModal = mutableStateOf<ChoosePeriodModalData?>(null)

    @Composable
    override fun uiState(): TransferState {
        return TransferState(
            period = getPeriod(),
            baseCurrency = getBaseCurrency(),
            currency = getCurrency(),
            categories = getCategories(),
            accounts = getAccounts(),
            account = getAccount(),
            category = getCategory(),
            balance = getBalance(),
            balanceBaseCurrency = getBalanceBaseCurrency(),
            income = getIncome(),
            expenses = getExpenses(),
            initWithTransactions = getInitWithTransactions(),
            treatTransfersAsIncomeExpense = getTreatTransfersAsIncomeExpense(),
            history = getHistory(),
            upcoming = getUpcoming(),
            upcomingExpanded = getUpcomingExpanded(),
            upcomingIncome = getUpcomingIncome(),
            upcomingExpenses = getUpcomingExpenses(),
            overdue = getOverdue(),
            overdueExpanded = getOverdueExpanded(),
            overdueIncome = getOverdueIncome(),
            overdueExpenses = getOverdueExpenses(),
            enableDeletionButton = getEnableDeletionButton(),
            skipAllModalVisible = getSkipAllModalVisible(),
            deleteModal1Visible = getDeleteModal1Visible(),
            choosePeriodModal = getChoosePeriodModal()
        )
    }

    @Composable
    private fun getPeriod(): TimePeriod {
        return period.value
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency.value
    }

    @Composable
    private fun getAccount(): LegacyAccount? {
        return account.value
    }

    @Composable
    private fun getCurrency(): String {
        return currency.value
    }

    @Composable
    private fun getCategories(): ImmutableList<Category> {
        return categories.value
    }

    @Composable
    private fun getAccounts(): ImmutableList<LegacyAccount> {
        return accounts.value
    }

    @Composable
    private fun getCategory(): Category? {
        return category.value
    }

    @Composable
    private fun getBalance(): Double {
        return balance.doubleValue
    }

    @Composable
    private fun getBalanceBaseCurrency(): Double? {
        return balanceBaseCurrency.value
    }

    @Composable
    private fun getIncome(): Double {
        return income.doubleValue
    }

    @Composable
    private fun getExpenses(): Double {
        return expenses.doubleValue
    }

    @Composable
    private fun getInitWithTransactions(): Boolean {
        return initWithTransactions.value
    }

    @Composable
    private fun getTreatTransfersAsIncomeExpense(): Boolean {
        return treatTransfersAsIncomeExpense.value
    }

    @Composable
    private fun getUpcomingExpenses(): Double {
        return upcomingExpenses.doubleValue
    }

    @Composable
    private fun getUpcoming(): ImmutableList<Transaction> {
        return upcoming.value
    }

    @Composable
    private fun getUpcomingExpanded(): Boolean {
        return upcomingExpanded.value
    }

    @Composable
    private fun getUpcomingIncome(): Double {
        return upcomingIncome.doubleValue
    }

    @Composable
    private fun getHistory(): ImmutableList<TransactionHistoryItem> {
        return history.value
    }

    @Composable
    private fun getOverdue(): ImmutableList<Transaction> {
        return overdue.value
    }

    @Composable
    private fun getOverdueExpanded(): Boolean {
        return overdueExpanded.value
    }

    @Composable
    private fun getOverdueIncome(): Double {
        return overdueIncome.doubleValue
    }

    @Composable
    private fun getOverdueExpenses(): Double {
        return overdueExpenses.doubleValue
    }

    @Composable
    private fun getEnableDeletionButton(): Boolean {
        return enableDeletionButton.value
    }

    @Composable
    private fun getSkipAllModalVisible(): Boolean {
        return skipAllModalVisible.value
    }

    @Composable
    private fun getDeleteModal1Visible(): Boolean {
        return deleteModal1Visible.value
    }

    @Composable
    private fun getChoosePeriodModal(): ChoosePeriodModalData? {
        return choosePeriodModal.value
    }

    override fun onEvent(event: TransfersEvent) {
        when (event) {
            is TransfersEvent.Delete -> delete(event.screen)
            is TransfersEvent.EditAccount -> editAccount(
                event.screen,
                event.account,
                event.newBalance
            )

            is TransfersEvent.EditCategory -> editCategory(event.updatedCategory)
            is TransfersEvent.NextMonth -> nextMonth(event.screen)
            is TransfersEvent.PayOrGet -> payOrGet(event.screen, event.transaction)
            is TransfersEvent.PreviousMonth -> previousMonth(event.screen)
            is TransfersEvent.SetPeriod -> setPeriod(event.screen, event.period)
            is TransfersEvent.SkipTransaction -> skipTransaction(event.screen, event.transaction)
            is TransfersEvent.SkipTransfers -> skipTransactions(
                event.screen,
                event.transactions
            )

            is TransfersEvent.UpdateAccountDeletionState -> updateAccountDeletionState(
                event.confirmationText
            )

            is TransfersEvent.SetOverdueExpanded -> setOverdueExpanded(event.expanded)
            is TransfersEvent.SetUpcomingExpanded -> setUpcomingExpanded(event.expanded)
            is TransfersEvent.SetSkipAllModalVisible -> setSkipAllModalVisible(event.visible)
            is TransfersEvent.OnDeleteModal1Visible -> setDeleteModal1Visible(event.delete)
            is TransfersEvent.OnChoosePeriodModalData -> setChoosePeriodModalData(event.data)
        }
    }

    private suspend fun initForAccount(accountId: UUID) {
        val initialAccount = ioThread {
            accountDao.findById(accountId)?.toLegacyDomain() ?: error("account not found")
        }
        account.value = initialAccount
        val range = period.value.toRange(mysaveContext.startDayOfMonth)

        if (initialAccount.currency.isNotNullOrBlank()) {
            currency.value = initialAccount.currency!!
        }

        val account = accountRepository.findById(AccountId(accountId)) ?: error("account not found")

        val balanceValue = calcAccBalanceAct(
            CalcAccBalanceAct.Input(
                account = account
            )
        ).balance.toDouble()
        balance.doubleValue = balanceValue
        if (baseCurrency.value != currency.value) {
            balanceBaseCurrency.value = exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency.value,
                        fromCurrency = currency.value.toOption()
                    ),
                    amount = balanceValue.toBigDecimal()
                )
            ).orNull()?.toDouble()
        }

        val includeTransfersInCalc =
            sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)

        val incomeExpensePair = calcAccIncomeExpenseAct(
            CalcAccIncomeExpenseAct.Input(
                account = account,
                range = range.toCloseTimeRange(),
                includeTransfersInCalc = includeTransfersInCalc
            )
        ).incomeExpensePair
        income.doubleValue = incomeExpensePair.income.toDouble()
        expenses.doubleValue = incomeExpensePair.expense.toDouble()

        history.value = (
                accTrnsAct then {
                    trnsWithDateDivsAct(
                        LegacyTrnsWithDateDivsAct.Input(
                            baseCurrency = baseCurrency.value,
                            transactions = with(transactionMapper) {
                                it.map {
                                    val tags =
                                        tagRepository.findByIds(it.tags).toImmutableLegacyTags()
                                    it.toEntity().toLegacyDomain(tags = tags)
                                }
                            }
                        )
                    )
                }
                )(
            AccTrnsAct.Input(
                accountId = initialAccount.id,
                range = range.toCloseTimeRange()
            )
        ).toImmutableList()

        // Upcoming
        upcomingIncome.doubleValue = ioThread {
            accountLogic.calculateUpcomingIncome(initialAccount, range)
        }

        upcomingExpenses.doubleValue = ioThread {
            accountLogic.calculateUpcomingExpenses(initialAccount, range)
        }

        upcoming.value = ioThread {
            with(transactionMapper) {
                accountLogic.upcoming(initialAccount, range).map { it.toEntity().toLegacyDomain() }
            }.toImmutableList()
        }

        // Overdue
        overdueIncome.doubleValue = ioThread {
            accountLogic.calculateOverdueIncome(initialAccount, range)
        }

        overdueExpenses.doubleValue = ioThread {
            accountLogic.calculateOverdueExpenses(initialAccount, range)
        }

        overdue.value = ioThread {
            with(transactionMapper) {
                accountLogic.overdue(initialAccount, range).map {
                    it.toEntity().toLegacyDomain()
                }.toImmutableList()
            }
        }
    }

    private suspend fun initForCategory(categoryId: UUID, accountFilterList: List<UUID>) {
        val accountFilterSet = accountFilterList.toSet()
        val initialCategory = ioThread {
            categoryRepository.findById(CategoryId(categoryId)) ?: error("category not found")
        }
        category.value = initialCategory
        val range = period.value.toRange(mysaveContext.startDayOfMonth)

        balance.doubleValue = ioThread {
            categoryLogic.calculateCategoryBalance(initialCategory, range, accountFilterSet)
        }

        income.doubleValue = ioThread {
            categoryLogic.calculateCategoryIncome(initialCategory, range, accountFilterSet)
        }

        expenses.doubleValue = ioThread {
            categoryLogic.calculateCategoryExpenses(initialCategory, range, accountFilterSet)
        }

        history.value = ioThread {
            categoryLogic.historyByCategoryAccountWithDateDividers(
                initialCategory,
                range,
                accountFilterSet = accountFilterList.toSet(),
            ).toImmutableList()
        }

        // Upcoming
        // TODO: Rework Upcoming to FP
        upcomingIncome.doubleValue = ioThread {
            categoryLogic.calculateUpcomingIncomeByCategory(initialCategory, range)
        }

        upcomingExpenses.doubleValue = ioThread {
            categoryLogic.calculateUpcomingExpensesByCategory(initialCategory, range)
        }

        upcoming.value = ioThread {
            categoryLogic.upcomingByCategoryLegacy(initialCategory, range).toImmutableList()
        }

        // Overdue
        // TODO: Rework Overdue to FP
        overdueIncome.doubleValue = ioThread {
            categoryLogic.calculateOverdueIncomeByCategory(initialCategory, range)
        }

        overdueExpenses.doubleValue = ioThread {
            categoryLogic.calculateOverdueExpensesByCategory(initialCategory, range)
        }

        overdue.value =
            ioThread {
                categoryLogic.overdueByCategoryLegacy(initialCategory, range).toImmutableList()
            }
    }

    private suspend fun initForCategoryWithTransactions(
        categoryId: UUID,
        accountFilterList: List<UUID>,
        transactions: List<Transaction>,
    ) {
        computationThread {
            initWithTransactions.value = true

            val trans = transactions.filter {
                it.type != TransactionType.TRANSFER && it.categoryId == categoryId
            }

            val accountFilterSet = accountFilterList.toSet()
            val initialCategory = ioThread {
                categoryRepository.findById(CategoryId(categoryId)) ?: error("category not found")
            }
            category.value = initialCategory
            val range = period.value.toRange(mysaveContext.startDayOfMonth)

            val incomeTrans = transactions.filter {
                it.categoryId == categoryId && it.type == TransactionType.INCOME
            }

            val expenseTrans = transactions.filter {
                it.categoryId == categoryId && it.type == TransactionType.EXPENSE
            }

            balance.value = ioThread {
                categoryLogic.calculateCategoryBalance(
                    initialCategory,
                    range,
                    accountFilterSet,
                    transactions = trans
                )
            }

            income.value = ioThread {
                categoryLogic.calculateCategoryIncome(
                    incomeTransaction = incomeTrans,
                    accountFilterSet = accountFilterSet
                )
            }

            expenses.doubleValue = ioThread {
                categoryLogic.calculateCategoryExpenses(
                    expenseTransactions = expenseTrans,
                    accountFilterSet = accountFilterSet
                )
            }

            history.value = ioThread {
                categoryLogic.historyByCategoryAccountWithDateDividers(
                    initialCategory,
                    range,
                    accountFilterSet = accountFilterList.toSet(),
                    transactions = trans
                ).toImmutableList()
            }

            // Upcoming
            // TODO: Rework Upcoming to FP
            upcomingIncome.doubleValue = ioThread {
                categoryLogic.calculateUpcomingIncomeByCategory(initialCategory, range)
            }

            upcomingExpenses.doubleValue = ioThread {
                categoryLogic.calculateUpcomingExpensesByCategory(initialCategory, range)
            }

            upcoming.value = ioThread {
                categoryLogic.upcomingByCategoryLegacy(initialCategory, range).toImmutableList()
            }

            // Overdue
            // TODO: Rework Overdue to FP
            overdueIncome.doubleValue = ioThread {
                categoryLogic.calculateOverdueIncomeByCategory(initialCategory, range)
            }

            overdueExpenses.doubleValue = ioThread {
                categoryLogic.calculateOverdueExpensesByCategory(initialCategory, range)
            }

            overdue.value =
                ioThread {
                    categoryLogic.overdueByCategoryLegacy(initialCategory, range).toImmutableList()
                }
        }
    }

    private suspend fun initForUnspecifiedCategory() {
        val range = period.value.toRange(mysaveContext.startDayOfMonth)

        balance.doubleValue = ioThread {
            categoryLogic.calculateUnspecifiedBalance(range)
        }

        income.doubleValue = ioThread {
            categoryLogic.calculateUnspecifiedIncome(range)
        }

        expenses.doubleValue = ioThread {
            categoryLogic.calculateUnspecifiedExpenses(range)
        }

        history.value = ioThread {
            categoryLogic.historyUnspecified(range).toImmutableList()
        }

        // Upcoming
        upcomingIncome.doubleValue = ioThread {
            categoryLogic.calculateUpcomingIncomeUnspecified(range)
        }

        upcomingExpenses.value = ioThread {
            categoryLogic.calculateUpcomingExpensesUnspecified(range)
        }

        upcoming.value = ioThread {
            categoryLogic.upcomingUnspecifiedLegacy(range).toImmutableList()
        }

        // Overdue
        overdueIncome.doubleValue = ioThread {
            categoryLogic.calculateOverdueIncomeUnspecified(range)
        }

        overdueExpenses.doubleValue = ioThread {
            categoryLogic.calculateOverdueExpensesUnspecified(range)
        }

        overdue.value = ioThread { categoryLogic.overdueUnspecifiedLegacy(range).toImmutableList() }
    }

    private suspend fun initForAccountTransfersCategory(
        accountFilterList: List<UUID>,
        transactions: List<Transaction>,
    ) {
        initWithTransactions.value = true
        val accountTransferCategory = Category(
            name = NotBlankTrimmedString.unsafe(stringRes(R.string.account_transfers)),
            color = ColorInt(RedLight.toArgb()),
            icon = IconAsset.unsafe("transfer"),
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
        category.value = accountTransferCategory
        val accountFilterIdSet = accountFilterList.toHashSet()
        val trans = transactions.filter {
            it.categoryId == null && (
                    accountFilterIdSet.contains(it.accountId) || accountFilterIdSet.contains(
                        it.toAccountId
                    )
                    ) && it.type == TransactionType.TRANSFER
        }

        val historyIncomeExpense = calcTrnsIncomeExpenseAct(
            LegacyCalcTrnsIncomeExpenseAct.Input(
                transactions = trans,
                accounts = accountFilterList.mapNotNull { accID -> accounts.value.find { it.id == accID } },
                baseCurrency = baseCurrency.value
            )
        )

        income.doubleValue = historyIncomeExpense.transferIncome.toDouble()
        expenses.doubleValue = historyIncomeExpense.transferExpense.toDouble()
        balance.doubleValue = income.doubleValue - expenses.doubleValue
        history.value = trnsWithDateDivsAct(
            LegacyTrnsWithDateDivsAct.Input(
                baseCurrency = baseCurrency.value,
                transactions = transactions
            )
        ).toImmutableList()
    }

    private fun reset() {
        account.value = null
        category.value = null
    }

    private fun setUpcomingExpanded(expanded: Boolean) {
        upcomingExpanded.value = expanded
    }

    private fun setOverdueExpanded(expanded: Boolean) {
        overdueExpanded.value = expanded
    }

    private fun setPeriod(
        screen: TransactScrin,
        period: TimePeriod,
    ) {
        start(
            screen = screen,
            timePeriod = period,
            reset = false
        )
    }

    private fun setSkipAllModalVisible(visible: Boolean) {
        skipAllModalVisible.value = visible
    }

    private fun nextMonth(screen: TransactScrin) {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            start(
                screen = screen,
                timePeriod = month.incrementMonthPeriod(mysaveContext, 1L, year),
                reset = false
            )
        }
    }

    private fun previousMonth(screen: TransactScrin) {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            start(
                screen = screen,
                timePeriod = month.incrementMonthPeriod(mysaveContext, -1L, year),
                reset = false
            )
        }
    }

    private fun delete(screen: TransactScrin) {
        viewModelScope.launch {
            when {
                screen.accountId != null -> {
                    deleteAccount(screen.accountId!!)
                }

                screen.categoryId != null -> {
                    deleteCategory(screen.categoryId!!)
                }
            }
        }
    }

    private suspend fun deleteAccount(accountId: UUID) {
        ioThread {
            transactionRepository.deleteAllByAccountId(accountId = AccountId(accountId))
            plannedPaymentRuleWriter.deletedByAccountId(accountId = accountId)
            accountRepository.deleteById(AccountId(accountId))

            nav.back()
        }
    }

    private suspend fun deleteCategory(categoryId: UUID) {
        ioThread {
            categoryWriter.deleteById(categoryId)
            categoryRepository.deleteById(CategoryId(categoryId))

            nav.back()
        }
    }

    private fun setDeleteModal1Visible(delete: Boolean) {
        deleteModal1Visible.value = delete
    }

    private fun setChoosePeriodModalData(data: ChoosePeriodModalData?) {
        choosePeriodModal.value = data
    }

    private fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            categoryCreator.editCategory(updatedCategory) {
                category.value = it
            }
        }
    }

    private fun editAccount(
        screen: TransactScrin,
        account: LegacyAccount,
        newBalance: Double,
    ) {
        viewModelScope.launch {
            accountCreator.editAccount(account, newBalance) {
                start(
                    screen = screen,
                    timePeriod = period.value,
                    reset = false
                )
            }
        }
    }

    private fun payOrGet(screen: TransactScrin, transaction: Transaction) {
        viewModelScope.launch {
            plannedPaymentsLogic.payOrGetLegacy(transaction = transaction) {
                start(
                    screen = screen,
                    reset = false
                )
            }
        }
    }

    private fun skipTransaction(screen: TransactScrin, transaction: Transaction) {
        viewModelScope.launch {
            plannedPaymentsLogic.payOrGetLegacy(
                transaction = transaction,
                skipTransaction = true
            ) {
                start(
                    screen = screen,
                    reset = false
                )
            }
        }
    }

    private fun skipTransactions(screen: TransactScrin, transactions: List<Transaction>) {
        viewModelScope.launch {
            plannedPaymentsLogic.payOrGetLegacy(
                transactions = transactions,
                skipTransaction = true
            ) {
                start(
                    screen = screen,
                    reset = false
                )
            }
        }
    }

    private fun updateAccountDeletionState(confirmationText: String) {
        accountNameConfirmation.value = selectEndTextFieldValue(confirmationText)
        enableDeletionButton.value = account.value?.name == confirmationText ||
                category.value?.name?.value == confirmationText
    }

    fun start(
        screen: TransactScrin,
        timePeriod: TimePeriod? = mysaveContext.selectedPeriod,
        reset: Boolean = true,
    ) {
        if (reset) {
            reset()
        }

        viewModelScope.launch {
            period.value = timePeriod ?: mysaveContext.selectedPeriod

            val baseCurrencyValue = baseCurrencyAct(Unit)
            baseCurrency.value = baseCurrencyValue
            currency.value = baseCurrency.value

            categories.value = categoryRepository.findAll().toImmutableList()
            accounts.value = accountsAct(Unit)
            initWithTransactions.value = false
            treatTransfersAsIncomeExpense.value =
                sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)

            when {
                screen.accountId != null -> {
                    initForAccount(screen.accountId!!)
                }

                screen.categoryId != null && screen.transactions.isEmpty() -> {
                    initForCategory(screen.categoryId!!, screen.accountIdFilterList)
                }
                // unspecifiedCategory==false is explicitly checked to accommodate for a temp
                // AccountTransfers Category during Reports Screen
                screen.categoryId != null && screen.transactions.isNotEmpty() &&
                        screen.unspecifiedCategory == false -> {
                    initForCategoryWithTransactions(
                        screen.categoryId!!,
                        screen.accountIdFilterList,
                        screen.transactions
                    )
                }

                screen.unspecifiedCategory == true && screen.transactions.isNotEmpty() -> {
                    initForAccountTransfersCategory(
                        screen.accountIdFilterList,
                        screen.transactions
                    )
                }

                screen.unspecifiedCategory == true -> {
                    initForUnspecifiedCategory()
                }

                else -> error("no id provided")
            }
        }
    }
}
