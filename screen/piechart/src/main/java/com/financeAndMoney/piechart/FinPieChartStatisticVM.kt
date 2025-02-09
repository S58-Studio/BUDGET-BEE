package com.financeAndMoney.piechart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.userInterface.ComposeViewModel
import com.financeAndMoney.legacy.MySaveCtx
import com.financeAndMoney.base.legacy.SharedPrefs
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.legacy.utils.ioThread
import com.financeAndMoney.navigation.FinPieChartStatisticSkrin
import com.financeAndMoney.piechart.vitendo.FinPieChartsActions
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.ChoosePeriodModalData
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
    private val finPieChartsActions: FinPieChartsActions,
    private val sharedPrefs: SharedPrefs
) : ComposeViewModel<FinPieChartStatisticState, FinPieChartStatisticEventi>() {

    private val treatTransfersAsIncomeExpense = mutableStateOf(false)
    private val transactionType = mutableStateOf(TransactionType.INCOME)
    private val period = mutableStateOf(TimePeriod())
    private val baseCurrency = mutableStateOf("")
    private val totalAmount = mutableDoubleStateOf(0.0)
    private val kategoriAmounts = mutableStateOf<ImmutableList<KategoriAmount>>(persistentListOf())
    private val selectedKategori = mutableStateOf<SelectedKategori?>(null)
    private val accountIdFilterList = mutableStateOf<ImmutableList<UUID>>(persistentListOf())
    private val showCloseButtonOnly = mutableStateOf(false)
    private val filterExcluded = mutableStateOf(false)
    private val transactions = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val choosePeriodModal = mutableStateOf<ChoosePeriodModalData?>(null)

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
        return transactionType.value
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
    private fun getTotalAmount(): Double {
        return totalAmount.doubleValue
    }

    @Composable
    private fun getCategoryAmounts(): ImmutableList<KategoriAmount> {
        return kategoriAmounts.value
    }

    @Composable
    private fun getSelectedCategory(): SelectedKategori? {
        return selectedKategori.value
    }

    @Composable
    private fun getAccountIdFilterList(): ImmutableList<UUID> {
        return accountIdFilterList.value
    }

    @Composable
    private fun getShowCloseButtonOnly(): Boolean {
        return showCloseButtonOnly.value
    }

    @Composable
    private fun getFilterExcluded(): Boolean {
        return filterExcluded.value
    }

    @Composable
    private fun getTransactions(): ImmutableList<Transaction> {
        return transactions.value
    }

    @Composable
    private fun getChoosePeriodModal(): ChoosePeriodModalData? {
        return choosePeriodModal.value
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
        treatTransfersAsIncomeExpense.value = transfersAsIncomeExpenseValue
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

        period.value = periodValue
        transactionType.value = type
        accountIdFilterList.value = accountIdFilterListValue
        filterExcluded.value = filterExcludedValue
        transactions.value = transactionsValue
        showCloseButtonOnly.value = transactionsValue.isNotEmpty()
        baseCurrency.value = baseCurrencyValue
    }

    private suspend fun load(
        periodValue: TimePeriod
    ) {
        val type = transactionType.value
        val accountIdFilterList = accountIdFilterList.value
        val transactions = transactions.value
        val baseCurrency = baseCurrency.value
        val range = periodValue.toRange(ivyContext.startDayOfMonth)

        val treatTransferAsIncExp =
            sharedPrefs.getBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                false
            ) && accountIdFilterList.isNotEmpty() && treatTransfersAsIncomeExpense.value

        val finPieChartsActionsOutput = ioThread {
            finPieChartsActions(
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

        val totalAmountValue = finPieChartsActionsOutput.totalAmount
        val categoryAmountsValue = finPieChartsActionsOutput.kategoriAmounts

        period.value = periodValue
        totalAmount.doubleValue = totalAmountValue
        kategoriAmounts.value = categoryAmountsValue
        selectedKategori.value = null
    }

    private suspend fun onSetPeriod(periodValue: TimePeriod) {
        ivyContext.updateSelectedPeriodInMemory(periodValue)
        load(
            periodValue = periodValue
        )
    }

    private suspend fun nextMonth() {
        val month = period.value.month
        val year = period.value.year ?: com.financeAndMoney.legacy.utils.dateNowUTC().year
        if (month != null) {
            load(
                periodValue = month.incrementMonthPeriod(ivyContext, 1L, year)
            )
        }
    }

    private suspend fun previousMonth() {
        val month = period.value.month
        val year = period.value.year ?: com.financeAndMoney.legacy.utils.dateNowUTC().year
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

        choosePeriodModal.value = choosePeriodModalData
    }

    private suspend fun onCategoryClicked(clickedCategory: Category?) {
        val selectedKategoriValue = if (clickedCategory == selectedKategori.value?.category) {
            null
        } else {
            clickedCategory?.let { SelectedKategori(category = it) }
        }

        val existingCategoryAmounts = kategoriAmounts.value
        val newCategoryAmounts = if (selectedKategoriValue != null) {
            existingCategoryAmounts
                .sortedByDescending { it.amount }
                .sortedByDescending {
                    selectedKategoriValue.category == it.category
                }
        } else {
            existingCategoryAmounts.sortedByDescending {
                it.amount
            }
        }.toImmutableList()

        selectedKategori.value = selectedKategoriValue
        kategoriAmounts.value = newCategoryAmounts
    }
}
