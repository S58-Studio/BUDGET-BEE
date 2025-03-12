package com.oneSaver.budgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.budgets.model.DisplayBajeti
import com.oneSaver.data.database.dao.write.WriteBudgetDao
import com.oneSaver.data.model.Expense
import com.oneSaver.data.model.Income
import com.oneSaver.data.model.Transaction
import com.oneSaver.data.model.Transfer
import com.oneSaver.data.temp.migration.getAccountId
import com.oneSaver.data.temp.migration.getValue
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.data.model.Category
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.legacy.frp.sumOfSuspend
import com.oneSaver.legacy.data.model.FromToTimeRange
import com.oneSaver.legacy.data.model.toCloseTimeRange
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.Budget
import com.oneSaver.legacy.domain.deprecated.logic.BudgetCreator
import com.oneSaver.legacy.utils.isNotNullOrBlank
import com.oneSaver.allStatus.domain.action.account.AccountsAct
import com.oneSaver.allStatus.domain.action.budget.BudgetsAct
import com.oneSaver.allStatus.domain.action.exchange.ExchangeAct
import com.oneSaver.allStatus.domain.action.settings.BaseCurrencyAct
import com.oneSaver.legacy.domain.action.transaction.HistoryTrnsAct
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateBudgetData
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeData
import com.oneSaver.allStatus.domain.pure.transaction.trnCurrency
import com.oneSaver.base.time.TimeConverter
import com.oneSaver.base.time.TimeProvider
import com.oneSaver.legacy.utils.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@Stable
@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val budgetWriter: WriteBudgetDao,
    private val budgetCreator: BudgetCreator,
    private val ivyContext: com.oneSaver.legacy.MySaveCtx,
    private val accountsAct: AccountsAct,
    private val categoryRepository: CategoryRepository,
    private val budgetsAct: BudgetsAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val historyTrnsAct: HistoryTrnsAct,
    private val exchangeAct: ExchangeAct,
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
) : ComposeViewModel<BajetiSkriniState, BajetiSkriniEventi>() {

    private val baseCurrency = mutableStateOf("")
    private val timeRange = mutableStateOf<FromToTimeRange?>(null)
    private val budgets = mutableStateOf<ImmutableList<DisplayBajeti>>(persistentListOf())
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val categoryBudgetsTotal = mutableDoubleStateOf(0.0)
    private val appBudgetMax = mutableDoubleStateOf(0.0)
    private val totalRemainingBudget = mutableDoubleStateOf(0.0)
    private val reorderModalVisible = mutableStateOf(false)
    private val budgetModalData = mutableStateOf<BudgetModalData?>(null)

    @Composable
    override fun uiState(): BajetiSkriniState {
        LaunchedEffect(Unit) {
            start()
        }

        return BajetiSkriniState(
            baseCurrency = getBaseCurrency(),
            categories = getCategories(),
            accounts = getAccounts(),
            budgets = getBudgets(),
            categoryBudgetsTotal = getCategoryBudgetsTotal(),
            appBudgetMax = getAppBudgetMax(),
            totalRemainingBudgetText = getTotalRemainingBudgetText(),
            timeRange = getTimeRange(),
            reorderModalVisible = getReorderModalVisible(),
            budgetModalData = getBudgetModalData()
        )
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency.value
    }

    @Composable
    private fun getTimeRange(): FromToTimeRange? {
        return timeRange.value
    }

    @Composable
    private fun getCategories(): ImmutableList<Category> {
        return categories.value
    }

    @Composable
    private fun getAccounts(): ImmutableList<Account> {
        return accounts.value
    }

    @Composable
    private fun getBudgets(): ImmutableList<DisplayBajeti> {
        return budgets.value
    }

    @Composable
    private fun getReorderModalVisible(): Boolean {
        return reorderModalVisible.value
    }

    @Composable
    private fun getCategoryBudgetsTotal(): Double {
        return categoryBudgetsTotal.doubleValue
    }

    @Composable
    private fun getAppBudgetMax(): Double {
        return appBudgetMax.doubleValue
    }

    @Composable
    private fun getTotalRemainingBudgetText(): String? {
        val budgetExceeded = totalRemainingBudget.doubleValue < 0
        return when {
            categoryBudgetsTotal.doubleValue > 0 -> stringResource(
                if (budgetExceeded) R.string.budget_exceeded_info else R.string.total_budget_info,
                abs(totalRemainingBudget.doubleValue).format(baseCurrency.value),
                baseCurrency.value
            )

            else -> null
        }
    }

    @Composable
    private fun getBudgetModalData(): BudgetModalData? {
        return budgetModalData.value
    }

    override fun onEvent(event: BajetiSkriniEventi) {
        when (event) {
            is BajetiSkriniEventi.OnCreateBudget -> {
                createBudget(event.budgetData)
            }

            is BajetiSkriniEventi.OnEditBudget -> {
                editBudget(event.budget)
            }

            is BajetiSkriniEventi.OnDeleteBudget -> {
                deleteBudget(event.budget)
            }

            is BajetiSkriniEventi.OnReorder -> {
                reorder(event.newOrder)
            }

            is BajetiSkriniEventi.OnReorderModalVisible -> {
                reorderModalVisible.value = event.visible
            }

            is BajetiSkriniEventi.OnBudgetModalData -> {
                budgetModalData.value = event.budgetModalData
            }
        }
    }

    private fun start() {
        viewModelScope.launch {
            categories.value = categoryRepository.findAll().toImmutableList()
            val accounts = accountsAct(Unit)
            val baseCurrency = baseCurrencyAct(Unit)
            val startDateOfMonth = ivyContext.initStartDayOfMonthInMemory(sharedPrefs = sharedPrefs)
            val timeRange = com.oneSaver.legacy.data.model.TimePeriod.currentMonth(
                startDayOfMonth = startDateOfMonth
            ).toRange(startDateOfMonth = startDateOfMonth, timeConverter, timeProvider)
            val budgets = budgetsAct(Unit)

            appBudgetMax.doubleValue = budgets
                .filter { it.categoryIdsSerialized.isNullOrBlank() }
                .maxOfOrNull { it.amount } ?: 0.0

            categoryBudgetsTotal.doubleValue = budgets
                .filter { it.categoryIdsSerialized.isNotNullOrBlank() }
                .sumOf { it.amount }

            this@BudgetViewModel.budgets.value = com.oneSaver.legacy.utils.ioThread {
                budgets.map {
                    DisplayBajeti(
                        budget = it,
                        spentAmount = calculateSpentAmount(
                            budget = it,
                            transactions = historyTrnsAct(timeRange.toCloseTimeRange()),
                            accounts = accounts,
                            baseCurrencyCode = baseCurrency
                        )
                    )
                }.toImmutableList()
            }
            totalRemainingBudget.doubleValue = calculateTotalRemainingBudget(
                budgets = this@BudgetViewModel.budgets.value,
                categoryBudgetsTotal = categoryBudgetsTotal.doubleValue
            )
            this@BudgetViewModel.accounts.value = accounts
            this@BudgetViewModel.baseCurrency.value = baseCurrency
            this@BudgetViewModel.timeRange.value = timeRange
        }
    }

    private suspend fun calculateSpentAmount(
        budget: Budget,
        transactions: List<Transaction>,
        baseCurrencyCode: String,
        accounts: List<Account>
    ): Double {
        // TODO: Re-work this by creating an FPAction for it
        val accountsFilter = budget.parseAccountIds()
        val categoryFilter = budget.parseCategoryIds()

        return transactions
            .filter { accountsFilter.isEmpty() || accountsFilter.contains(it.getAccountId()) }
            .filter { categoryFilter.isEmpty() || categoryFilter.contains(it.category?.value) }
            .sumOfSuspend {
                when (it) {
                    is Income -> {
                        0.0 // ignore income
                    }

                    is Expense -> {
                        // increment spent amount
                        exchangeAct(
                            ExchangeAct.Input(
                                data = ExchangeData(
                                    baseCurrency = baseCurrencyCode,
                                    fromCurrency = trnCurrency(it, accounts, baseCurrencyCode)
                                ),
                                amount = it.getValue()
                            )
                        ).orNull()?.toDouble() ?: 0.0
                    }

                    is Transfer -> {
                        // ignore transfers for simplicity
                        0.0
                    }
                }
            }
    }

    private fun createBudget(data: CreateBudgetData) {
        viewModelScope.launch {
            budgetCreator.createBudget(data) {
                start()
            }
        }
    }

    private fun editBudget(budget: Budget) {
        viewModelScope.launch {
            budgetCreator.editBudget(budget) {
                start()
            }
        }
    }

    private fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetCreator.deleteBudget(budget) {
                start()
            }
        }
    }

    private fun reorder(newOrder: List<DisplayBajeti>) {
        viewModelScope.launch {
            com.oneSaver.legacy.utils.ioThread {
                newOrder.forEachIndexed { index, item ->
                    budgetWriter.save(
                        item.budget.toEntity().copy(
                            orderId = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
            start()
        }
    }
}

fun calculateTotalRemainingBudget(
    budgets: ImmutableList<DisplayBajeti>,
    categoryBudgetsTotal: Double
): Double {
    return categoryBudgetsTotal - budgets
        .filter { it.budget.categoryIdsSerialized.isNotNullOrBlank() }
        .sumOf { it.spentAmount }
}

