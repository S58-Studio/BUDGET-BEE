package com.financeAndMoney.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.base.legacy.TransactionHistoryItem
import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.repository.CategoryRepository
import com.financeAndMoney.data.repository.mapper.TransactionMapper
import com.financeAndMoney.domains.usecase.Xchange.SyncXchangeRatesUseCase
import com.financeAndMoney.frp.fixUnit
import com.financeAndMoney.frp.then
import com.financeAndMoney.frp.thenInvokeAfter
import com.financeAndMoney.home.clientJourney.ClientJourneyCardModel
import com.financeAndMoney.home.clientJourney.ClientJourneyCardsProvider
import com.financeAndMoney.legacy.MySaveCtx
import com.financeAndMoney.legacy.data.AppBaseData
import com.financeAndMoney.legacy.data.BufferInfo
import com.financeAndMoney.legacy.data.LegacyDueSection
import com.financeAndMoney.legacy.data.model.MainTab
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.legacy.data.model.toCloseTimeRange
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.Settings
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import com.financeAndMoney.legacy.domain.action.settings.UpdateSettingsAct
import com.financeAndMoney.legacy.domain.action.viewmodel.home.ShouldHideIncomeAct
import com.financeAndMoney.legacy.utils.dateNowUTC
import com.financeAndMoney.legacy.utils.ioThread
import com.financeAndMoney.navigation.BalanceSkrin
import com.financeAndMoney.navigation.MainSkreen
import com.financeAndMoney.navigation.Navigation
import com.financeAndMoney.userInterface.ComposeViewModel
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.AccountsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.global.StartDayOfMonthAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.settings.CalcBufferDiffAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.settings.SettingsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.transaction.HistoryWithDateDivsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home.HasTrnsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home.OverdueAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home.ShouldHideBalanceAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home.UpcomingAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home.UpdateAccCacheAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home.UpdateCategoriesCacheAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.wallet.CalcIncomeExpenseAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.wallet.CalcWalletBalanceAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.PlannedPaymentsLogic
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.ClosedTimeRange
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.IncomeExpensePair
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@Stable
@HiltViewModel
class HomeVM @Inject constructor(
    private val mysaveContext: MySaveCtx,
    private val nav: Navigation,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val customerJourneyLogic: ClientJourneyCardsProvider,
    private val historyWithDateDivsAct: HistoryWithDateDivsAct,
    private val calcIncomeExpenseAct: CalcIncomeExpenseAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val settingsAct: SettingsAct,
    private val accountsAct: AccountsAct,
    private val categoryRepository: CategoryRepository,
    private val calcBufferDiffAct: CalcBufferDiffAct,
    private val upcomingAct: UpcomingAct,
    private val overdueAct: OverdueAct,
    private val hasTrnsAct: HasTrnsAct,
    private val startDayOfMonthAct: StartDayOfMonthAct,
    private val shouldHideBalanceAct: ShouldHideBalanceAct,
    private val shouldHideIncomeAct: ShouldHideIncomeAct,
    private val updateSettingsAct: UpdateSettingsAct,
    private val updateAccCacheAct: UpdateAccCacheAct,
    private val updateCategoriesCacheAct: UpdateCategoriesCacheAct,
    private val syncXchangeRatesUseCase: SyncXchangeRatesUseCase,
    private val transactionMapper: TransactionMapper
) : ComposeViewModel<NyumbaniState, NyumbaniEvent>() {
    private val currentTheme = mutableStateOf(Theme.AUTO)
    private val name = mutableStateOf("")
    private val period = mutableStateOf(mysaveContext.selectedPeriod)
    private val baseData = mutableStateOf(
        AppBaseData(
            baseCurrency = "",
            accounts = persistentListOf(),
            categories = persistentListOf()
        )
    )
    private val history = mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())
    private val stats = mutableStateOf(IncomeExpensePair.zero())
    private val balance = mutableStateOf(BigDecimal.ZERO)
    private val buffer = mutableStateOf(
        BufferInfo(
            amount = BigDecimal.ZERO,
            bufferDiff = BigDecimal.ZERO,
        )
    )
    private val upcoming = mutableStateOf(
        LegacyDueSection(
            trns = persistentListOf(),
            stats = IncomeExpensePair.zero(),
            expanded = false,
        )
    )
    private val overdue = mutableStateOf(
        LegacyDueSection(
            trns = persistentListOf(),
            stats = IncomeExpensePair.zero(),
            expanded = false,
        )
    )
    private val customerJourneyCards =
        mutableStateOf<ImmutableList<ClientJourneyCardModel>>(persistentListOf())
    private val hideBalance = mutableStateOf(false)
    private val hideIncome = mutableStateOf(false)
    private val expanded = mutableStateOf(true)

    @Composable
    override fun uiState(): NyumbaniState {
        LaunchedEffect(Unit) {
            start()
        }

        return NyumbaniState(
            theme = getTheme(),
            name = getName(),
            period = getPeriod(),
            baseData = getBaseData(),
            history = getHistory(),
            stats = getStats(),
            balance = getBalance(),
            buffer = getBuffer(),
            upcoming = getUpcoming(),
            overdue = getOverdue(),
            customerJourneyCards = getCustomerJourneyCards(),
            hideBalance = getHideBalance(),
            expanded = getExpanded(),
            hideIncome = getHideIncome()
        )
    }

    @Composable
    private fun getTheme(): Theme {
        return currentTheme.value
    }

    @Composable
    private fun getName(): String {
        return name.value
    }

    @Composable
    private fun getPeriod(): TimePeriod {
        return period.value
    }

    @Composable
    private fun getBaseData(): AppBaseData {
        return baseData.value
    }

    @Composable
    private fun getHistory(): ImmutableList<TransactionHistoryItem> {
        return history.value
    }

    @Composable
    private fun getStats(): IncomeExpensePair {
        return stats.value
    }

    @Composable
    private fun getBalance(): BigDecimal {
        return balance.value
    }

    @Composable
    private fun getBuffer(): BufferInfo {
        return buffer.value
    }

    @Composable
    private fun getUpcoming(): LegacyDueSection {
        return upcoming.value
    }

    @Composable
    private fun getOverdue(): LegacyDueSection {
        return overdue.value
    }

    @Composable
    private fun getCustomerJourneyCards(): ImmutableList<ClientJourneyCardModel> {
        return customerJourneyCards.value
    }

    @Composable
    private fun getHideBalance(): Boolean {
        return hideBalance.value
    }

    @Composable
    private fun getExpanded(): Boolean {
        return expanded.value
    }

    @Composable
    private fun getHideIncome(): Boolean {
        return hideIncome.value
    }

    override fun onEvent(event: NyumbaniEvent) {
        viewModelScope.launch {
            when (event) {
                NyumbaniEvent.BalanceClick -> onBalanceClick()
                NyumbaniEvent.HiddenBalanceClick -> onHiddenBalanceClick()
                NyumbaniEvent.HiddenIncomeClick -> onHiddenIncomeClick()
                is NyumbaniEvent.PayOrGetPlanned -> payOrGetPlanned(event.transaction)
                is NyumbaniEvent.SkipPlanned -> skipPlanned(event.transaction)
                is NyumbaniEvent.SkipAllPlanned -> skipAllPlanned(event.transactions)
                is NyumbaniEvent.SetPeriod -> setPeriod(event.period)
                NyumbaniEvent.SelectNextMonth -> onSelectNextMonth()
                NyumbaniEvent.SelectPreviousMonth -> onSelectPreviousMonth()
                is NyumbaniEvent.SetUpcomingExpanded -> setUpcomingExpanded(event.expanded)
                is NyumbaniEvent.SetOverdueExpanded -> setOverdueExpanded(event.expanded)
                is NyumbaniEvent.SetBuffer -> setBuffer(event.buffer)
                is NyumbaniEvent.SetCurrency -> setCurrency(event.currency).fixUnit()
                NyumbaniEvent.SwitchTheme -> switchTheme()
                is NyumbaniEvent.DismissCustomerJourneyCard -> dismissCustomerJourneyCard(event.card)
                is NyumbaniEvent.SetExpanded -> setExpanded(event.expanded)
            }
        }
    }

    private suspend fun start() {
        suspend {
            val startDay = startDayOfMonthAct(Unit)
            mysaveContext.initSelectedPeriodInMemory(
                startDayOfMonth = startDay
            )
        } thenInvokeAfter ::reload
    }

    // -----------------------------------------------------------------------------------
    private suspend fun reload(
        timePeriod: TimePeriod = mysaveContext.selectedPeriod
    ) = suspend {
        val settings = settingsAct(Unit)
        val hideBalance = shouldHideBalanceAct(Unit)
        val hideIncome = shouldHideIncomeAct(Unit)

        currentTheme.value = settings.theme
        name.value = settings.name
        period.value = timePeriod
        this.hideBalance.value = hideBalance
        this.hideIncome.value = hideIncome

        // This method is used to restore the theme when user imports locally backed up data
        mysaveContext.switchTheme(theme = settings.theme)

        Pair(settings, period.value.toRange(mysaveContext.startDayOfMonth).toCloseTimeRange())
    } then ::loadAppBaseData then ::loadIncomeExpenseBalance then
            ::loadBuffer then ::loadTrnHistory then
            ::loadDueTrns thenInvokeAfter ::loadCustomerJourney

    private suspend fun loadAppBaseData(
        input: Pair<Settings, ClosedTimeRange>
    ): Triple<Settings, ClosedTimeRange, List<Account>> =
        suspend {} then accountsAct then updateAccCacheAct then { accounts ->
            accounts
        } then { accounts ->
            val retrievedCategories = categoryRepository.findAll()
            val categories = updateCategoriesCacheAct(retrievedCategories)
            accounts to categories
        } thenInvokeAfter { (accounts, categories) ->
            val (settings, timeRange) = input

            baseData.value = AppBaseData(
                baseCurrency = settings.baseCurrency,
                categories = categories.toImmutableList(),
                accounts = accounts.toImmutableList()
            )

            Triple(settings, timeRange, accounts)
        }

    private suspend fun loadIncomeExpenseBalance(
        input: Triple<Settings, ClosedTimeRange, List<Account>>
    ): Triple<Settings, ClosedTimeRange, BigDecimal> {
        val (settings, timeRange, accounts) = input

        val incomeExpense = calcIncomeExpenseAct(
            CalcIncomeExpenseAct.Input(
                baseCurrency = settings.baseCurrency,
                accounts = accounts,
                range = timeRange
            )
        )

        val balanceAmount = calcWalletBalanceAct(
            CalcWalletBalanceAct.Input(baseCurrency = settings.baseCurrency)
        )

        balance.value = balanceAmount
        stats.value = incomeExpense

        return Triple(settings, timeRange, balanceAmount)
    }

    private suspend fun loadBuffer(
        input: Triple<Settings, ClosedTimeRange, BigDecimal>
    ): Pair<String, ClosedTimeRange> {
        val (settings, timeRange, balance) = input

        buffer.value = BufferInfo(
            amount = settings.bufferAmount,
            bufferDiff = calcBufferDiffAct(
                CalcBufferDiffAct.Input(
                    balance = balance,
                    buffer = settings.bufferAmount
                )
            )
        )

        return settings.baseCurrency to timeRange
    }

    private suspend fun loadTrnHistory(
        input: Pair<String, ClosedTimeRange>
    ): Pair<String, ClosedTimeRange> {
        val (baseCurrency, timeRange) = input

        history.value = historyWithDateDivsAct(
            HistoryWithDateDivsAct.Input(
                range = timeRange,
                baseCurrency = baseCurrency
            )
        )

        return baseCurrency to timeRange
    }

    private suspend fun loadDueTrns(
        input: Pair<String, ClosedTimeRange>
    ): Unit = suspend {
        UpcomingAct.Input(baseCurrency = input.first, range = input.second)
    } then upcomingAct then { result ->
        upcoming.value = LegacyDueSection(
            trns = with(transactionMapper) {
                result.upcomingTrns.map {
                    it.toEntity().toLegacyDomain()
                }.toImmutableList()
            },
            stats = result.upcoming,
            expanded = upcoming.value.expanded
        )
    } then {
        OverdueAct.Input(baseCurrency = input.first, toRange = input.second.to)
    } then overdueAct thenInvokeAfter { result ->
        overdue.value = LegacyDueSection(
            trns = with(transactionMapper) {
                result.overdueTrns.map {
                    it.toEntity().toLegacyDomain()
                }.toImmutableList()
            },
            stats = result.overdue,
            expanded = overdue.value.expanded
        )
    }

    private suspend fun loadCustomerJourney(unit: Unit) {
        customerJourneyCards.value = ioThread {
            customerJourneyLogic.loadCards().toImmutableList()
        }
    }
// -----------------------------------------------------------------

    private fun setUpcomingExpanded(expanded: Boolean) {
        upcoming.value = upcoming.value.copy(expanded = expanded)
    }

    private fun setOverdueExpanded(expanded: Boolean) {
        overdue.value = overdue.value.copy(expanded = expanded)
    }

    private suspend fun onBalanceClick() {
        val hasTransactions = hasTrnsAct(Unit)
        if (hasTransactions) {
            // has transfers show him "Balance" screen
            nav.navigateTo(BalanceSkrin)
        } else {
            // doesn't have transfers lead him to adjust mula balance
            mysaveContext.selectMainTab(MainTab.ACCOUNTS)
            nav.navigateTo(MainSkreen)
        }
    }

    private suspend fun onHiddenBalanceClick() {
        hideBalance.value = false

        // Showing Balance fow 5s
        delay(5000)

        hideBalance.value = true
    }

    private suspend fun onHiddenIncomeClick() {
        hideIncome.value = false

        // Showing Balance fow 5s
        delay(5000)

        hideIncome.value = true
    }

    private fun switchTheme() {
        viewModelScope.launch {
            settingsAct.getSettingsWithNextTheme().run {
                updateSettingsAct(this)
                mysaveContext.switchTheme(this.theme)
                currentTheme.value = this.theme
            }
        }
    }

    private fun setBuffer(newBuffer: Double) {
        viewModelScope.launch {
            val currentSettings =
                settingsAct.getSettings().copy(bufferAmount = newBuffer.toBigDecimal())
            updateSettingsAct(currentSettings)
            buffer.value = buffer.value.copy(amount = currentSettings.bufferAmount)
        }
    }

    private suspend fun setCurrency(newCurrency: String) = settingsAct then {
        it.copy(
            baseCurrency = newCurrency
        )
    } then updateSettingsAct then {
        // update exchange rates from POV of the new base currency
        AssetCode.from(newCurrency).onRight {
            syncXchangeRatesUseCase.sync(it)
        }
    } then {
        reload()
    }

    private suspend fun payOrGetPlanned(transaction: Transaction) {
        plannedPaymentsLogic.payOrGetLegacy(
            transaction = transaction,
            skipTransaction = false
        ) {
            reload()
        }
    }

    private suspend fun skipPlanned(transaction: Transaction) {
        plannedPaymentsLogic.payOrGetLegacy(
            transaction = transaction,
            skipTransaction = true
        ) {
            reload()
        }
    }

    private suspend fun skipAllPlanned(transactions: List<Transaction>) {
        plannedPaymentsLogic.payOrGetLegacy(
            transactions = transactions,
            skipTransaction = true
        ) {
            reload()
        }
    }

    private suspend fun dismissCustomerJourneyCard(card: ClientJourneyCardModel) = suspend {
        customerJourneyLogic.dismissCard(card)
    } thenInvokeAfter {
        reload()
    }

    private suspend fun onSelectNextMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        val period = month?.incrementMonthPeriod(mysaveContext, 1L, year = year)
        if (period != null) {
            setPeriod(period)
        }
    }

    private suspend fun onSelectPreviousMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        val period = month?.incrementMonthPeriod(mysaveContext, -1L, year = year)
        if (period != null) {
            setPeriod(period)
        }
    }

    private suspend fun setPeriod(period: TimePeriod) {
        reload(period)
    }

    private fun setExpanded(expanded: Boolean) {
        this.expanded.value = expanded
    }
}
