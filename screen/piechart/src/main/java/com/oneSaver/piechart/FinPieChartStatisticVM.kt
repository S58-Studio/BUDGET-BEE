package com.oneSaver.piechart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.database.dao.read.SettingsDao
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.data.model.Category
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.navigation.FinPieChartStatisticSkrin
import com.oneSaver.piechart.vitendo.FinPieChartsActions
import com.oneSaver.allStatus.userInterface.theme.modal.ChoosePeriodModalData
import com.oneSaver.base.time.TimeConverter
import com.oneSaver.base.time.TimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@Stable
@HiltViewModel
class FinPieChartStatisticVM @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivyContext: MySaveCtx,
    private val pieChartAct: FinPieChartsActions,
    private val sharedPrefs: SharedPrefs,
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
) : ComposeViewModel<FinPieChartStatisticState, FinPieChartStatisticEventi>() {

    private var treatTransfersAsIncomeExpense by mutableStateOf(false)
    private var transactionType by mutableStateOf(TransactionType.INCOME)
    private var period by mutableStateOf(TimePeriod())
    private var baseCurrency by mutableStateOf("")
    private var totalAmount by mutableDoubleStateOf(0.0)
    private var categoryAmounts by mutableStateOf<ImmutableList<KategoriAmount>>(persistentListOf())
    private var selectedCategory by mutableStateOf<SelectedKategori?>(null)
    private var accountIdFilterList by mutableStateOf<ImmutableList<UUID>>(persistentListOf())
    private var showCloseButtonOnly by mutableStateOf(false)
    private var filterExcluded by mutableStateOf(false)
    private var transactions by mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private var choosePeriodModal by mutableStateOf<ChoosePeriodModalData?>(null)

    @Composable
    override fun uiState(): FinPieChartStatisticState {
        return FinPieChartStatisticState(
            transactionType = getTransactionType(),
            period = getPeriod(),
            baseCurrency = getBaseCurrency(),
            totalAmount = getTotalAmount(),
            kategoriAmounts = getCategoryAmounts(),
            selectedKategori = getSelectedCategory(),
            accountIdFilterList = getAccountIdFilterList(),
            showCloseButtonOnly = getShowCloseButtonOnly(),
            filterExcluded = getFilterExcluded(),
            transactions = getTransactions(),
            choosePeriodModal = getChoosePeriodModal()
        )
    }

    @Composable
    private fun getTransactionType(): TransactionType {
        return transactionType
    }

    @Composable
    private fun getPeriod(): TimePeriod {
        return period
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency
    }

    @Composable
    private fun getTotalAmount(): Double {
        return totalAmount
    }

    @Composable
    private fun getCategoryAmounts(): ImmutableList<KategoriAmount> {
        return categoryAmounts
    }

    @Composable
    private fun getSelectedCategory(): SelectedKategori? {
        return selectedCategory
    }

    @Composable
    private fun getAccountIdFilterList(): ImmutableList<UUID> {
        return accountIdFilterList
    }

    @Composable
    private fun getShowCloseButtonOnly(): Boolean {
        return showCloseButtonOnly
    }

    @Composable
    private fun getFilterExcluded(): Boolean {
        return filterExcluded
    }

    @Composable
    private fun getTransactions(): ImmutableList<Transaction> {
        return transactions
    }

    @Composable
    private fun getChoosePeriodModal(): ChoosePeriodModalData? {
        return choosePeriodModal
    }

    override fun onEvent(event: FinPieChartStatisticEventi) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is FinPieChartStatisticEventi.OnSelectNextMonth -> nextMonth()
                is FinPieChartStatisticEventi.OnSelectPreviousMonth -> previousMonth()
                is FinPieChartStatisticEventi.OnSetPeriod -> onSetPeriod(event.timePeriod)
                is FinPieChartStatisticEventi.OnShowMonthModal -> configureMonthModal(event.timePeriod)
                is FinPieChartStatisticEventi.OnCategoryClicked -> onCategoryClicked(event.category)
                is FinPieChartStatisticEventi.OnStart -> start(event.screen)
            }
        }
    }

    private fun start(
        screen: FinPieChartStatisticSkrin
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            startInternally(
                period = ivyContext.selectedPeriod,
                type = screen.type,
                accountIdFilterList = screen.accountList,
                filterExclude = screen.filterExcluded,
                transactions = screen.transactions,
                transfersAsIncomeExpenseValue = screen.treatTransfersAsIncomeExpense
            )
        }
    }

    private suspend fun startInternally(
        period: TimePeriod,
        type: TransactionType,
        accountIdFilterList: ImmutableList<UUID>,
        filterExclude: Boolean,
        transactions: ImmutableList<Transaction>,
        transfersAsIncomeExpenseValue: Boolean
    ) {
        initialise(period, type, accountIdFilterList, filterExclude, transactions)
        treatTransfersAsIncomeExpense = transfersAsIncomeExpenseValue
        load(periodValue = period)
    }

    private suspend fun initialise(
        periodValue: TimePeriod,
        type: TransactionType,
        accountIdFilterListValue: ImmutableList<UUID>,
        filterExcludedValue: Boolean,
        transactionsValue: ImmutableList<Transaction>
    ) {
        val settings = ioThread { settingsDao.findFirst() }
        val baseCurrencyValue = settings.currency

        period = periodValue
        transactionType = type
        accountIdFilterList = accountIdFilterListValue
        filterExcluded = filterExcludedValue
        transactions = transactionsValue
        showCloseButtonOnly = transactionsValue.isNotEmpty()
        baseCurrency = baseCurrencyValue
    }

    private suspend fun load(
        periodValue: TimePeriod
    ) {
        val type = transactionType
        val accountIdFilterList = accountIdFilterList
        val transactions = transactions
        val baseCurrency = baseCurrency
        val range = periodValue.toRange(ivyContext.startDayOfMonth, timeConverter, timeProvider)

        val treatTransferAsIncExp =
            sharedPrefs.getBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                false
            ) && accountIdFilterList.isNotEmpty() && treatTransfersAsIncomeExpense

        val pieChartActOutput = ioThread {
            pieChartAct(
                FinPieChartsActions.Input(
                    baseCurrency = baseCurrency,
                    range = range,
                    type = type,
                    accountIdFilterList = accountIdFilterList,
                    treatTransferAsIncExp = treatTransferAsIncExp,
                    existingTransactions = transactions,
                    showAccountTransfersCategory = accountIdFilterList.isNotEmpty()
                )
            )
        }

        val totalAmountValue = pieChartActOutput.totalAmount
        val categoryAmountsValue = pieChartActOutput.kategoriAmounts

        period = periodValue
        totalAmount = totalAmountValue
        categoryAmounts = categoryAmountsValue
        selectedCategory = null
    }

    private suspend fun onSetPeriod(periodValue: TimePeriod) {
        ivyContext.updateSelectedPeriodInMemory(periodValue)
        load(
            periodValue = periodValue
        )
    }

    private suspend fun nextMonth() {
        val month = period.month
        val year = period.year ?: com.oneSaver.legacy.utils.dateNowUTC().year
        if (month != null) {
            load(
                periodValue = month.incrementMonthPeriod(ivyContext, 1L, year)
            )
        }
    }

    private suspend fun previousMonth() {
        val month = period.month
        val year = period.year ?: com.oneSaver.legacy.utils.dateNowUTC().year
        if (month != null) {
            load(
                periodValue = month.incrementMonthPeriod(ivyContext, -1L, year)
            )
        }
    }

    private suspend fun configureMonthModal(timePeriod: TimePeriod?) {
        val choosePeriodModalData = if (timePeriod != null) {
            ChoosePeriodModalData(period = timePeriod)
        } else {
            null
        }

        choosePeriodModal = choosePeriodModalData
    }

    private suspend fun onCategoryClicked(clickedCategory: Category?) {
        val selectedCategoryValue = if (clickedCategory == selectedCategory?.category) {
            null
        } else {
            clickedCategory?.let { SelectedKategori(category = it) }
        }

        val existingCategoryAmounts = categoryAmounts
        val newCategoryAmounts = if (selectedCategoryValue != null) {
            existingCategoryAmounts
                .sortedByDescending { it.amount }
                .sortedByDescending {
                    selectedCategoryValue.category == it.category
                }
        } else {
            existingCategoryAmounts.sortedByDescending {
                it.amount
            }
        }.toImmutableList()

        selectedCategory = selectedCategoryValue
        categoryAmounts = newCategoryAmounts
    }
}
