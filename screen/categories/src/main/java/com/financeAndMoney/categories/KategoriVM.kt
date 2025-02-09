package com.financeAndMoney.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.financeAndMoney.base.legacy.SharedPrefs
import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.userInterface.ComposeViewModel
import com.financeAndMoney.data.repository.CategoryRepository
import com.financeAndMoney.frp.action.thenMap
import com.financeAndMoney.frp.thenInvokeAfter
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.utils.ioThread
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.AccountsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.category.LegacyCategoryIncomeWithAccountFiltersAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.settings.BaseCurrencyAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.transaction.TrnsWithRangeAndAccFiltersAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.data.SortOrder
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.CategoryCreator
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateCategoryData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.CategoryModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryCreator: CategoryCreator,
    private val categoryRepository: CategoryRepository,
    private val ivyContext: com.financeAndMoney.legacy.MySaveCtx,
    private val sharedPrefs: SharedPrefs,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val accountsAct: AccountsAct,
    private val trnsWithRangeAndAccFiltersAct: TrnsWithRangeAndAccFiltersAct,
    private val categoryIncomeWithAccountFiltersAct: LegacyCategoryIncomeWithAccountFiltersAct,
) : ComposeViewModel<KategoriSkriniState, KategoriSkriniEventi>() {

    private val baseCurrency = mutableStateOf("")
    private val categories =
        mutableStateOf<ImmutableList<KategoriData>>(persistentListOf<KategoriData>())
    private val reorderModalVisible = mutableStateOf(false)
    private val categoryModalData = mutableStateOf<CategoryModalData?>(null)
    private val sortModalVisible = mutableStateOf(false)
    private val sortOrder = mutableStateOf(SortOrder.DEFAULT)

    @Composable
    override fun uiState(): KategoriSkriniState {
        LaunchedEffect(Unit) {
            start()
        }

        return KategoriSkriniState(
            baseCurrency = getBaseCurrency(),
            categories = getCategories(),
            reorderModalVisible = getReorderModalVisible(),
            categoryModalData = getCategoryModalData(),
            sortOrder = getSortOrder(),
            sortModalVisible = getSortModalVisible()
        )
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency.value
    }

    @Composable
    private fun getCategories(): ImmutableList<KategoriData> {
        return categories.value
    }

    @Composable
    private fun getReorderModalVisible(): Boolean {
        return reorderModalVisible.value
    }

    @Composable
    private fun getCategoryModalData(): CategoryModalData? {
        return categoryModalData.value
    }

    @Composable
    private fun getSortOrder(): SortOrder {
        return sortOrder.value
    }

    @Composable
    private fun getSortModalVisible(): Boolean {
        return sortModalVisible.value
    }

    private var allAccounts = emptyList<Account>()
    private var transactions = emptyList<Transaction>()

    private fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            initialise()
            loadCategories()
        }
    }

    private suspend fun initialise() {
        ioThread {
            val range = TimePeriod.currentMonth(
                startDayOfMonth = ivyContext.startDayOfMonth
            ).toRange(ivyContext.startDayOfMonth) // this must be monthly

            allAccounts = accountsAct(Unit)
            baseCurrency.value = baseCurrencyAct(Unit)

            transactions = trnsWithRangeAndAccFiltersAct(
                TrnsWithRangeAndAccFiltersAct.Input(
                    range = range,
                    accountIdFilterSet = suspend { allAccounts } thenMap { it.id }
                        thenInvokeAfter { it.toHashSet() }
                )
            )

            val sortOrder = SortOrder.from(
                sharedPrefs.getInt(
                    SharedPrefs.CATEGORY_SORT_ORDER,
                    SortOrder.DEFAULT.orderNum
                )
            )

            this.sortOrder.value = sortOrder
        }
    }

    private suspend fun loadCategories() {
        com.financeAndMoney.legacy.utils.scopedIOThread { scope ->
            val categories = categoryRepository.findAll().mapAsync(scope) {
                val catIncomeExpense = categoryIncomeWithAccountFiltersAct(
                    LegacyCategoryIncomeWithAccountFiltersAct.Input(
                        transactions = transactions,
                        accountFilterList = allAccounts,
                        category = it,
                        baseCurrency = baseCurrency.value
                    )
                )

                KategoriData(
                    category = it,
                    monthlyBalance = (catIncomeExpense.income - catIncomeExpense.expense).toDouble(),
                    monthlyIncome = catIncomeExpense.income.toDouble(),
                    monthlyExpenses = catIncomeExpense.expense.toDouble()
                )
            }

            val sortedList = sortList(categories, sortOrder.value).toImmutableList()

            this.categories.value = sortedList
        }
    }

    private suspend fun reorder(
        newOrder: List<KategoriData>,
        sortOrder: SortOrder = SortOrder.DEFAULT
    ) {
        val sortedList = sortList(newOrder, sortOrder).toImmutableList()

        if (sortOrder == SortOrder.DEFAULT) {
            ioThread {
                sortedList.forEachIndexed { index, categoryData ->
                    categoryRepository.save(categoryData.category.copy(orderNum = index.toDouble()))
                }
            }
        }

        ioThread {
            sharedPrefs.putInt(SharedPrefs.CATEGORY_SORT_ORDER, sortOrder.orderNum)
        }

        this.categories.value = sortedList
        this.sortOrder.value = sortOrder
    }

    private fun sortList(
        kategoriData: List<KategoriData>,
        sortOrder: SortOrder
    ): List<KategoriData> {
        return when (sortOrder) {
            SortOrder.DEFAULT -> kategoriData.sortedBy {
                it.category.orderNum
            }

            SortOrder.BALANCE_AMOUNT -> kategoriData.sortedByDescending {
                it.monthlyBalance
            }.partition { it.monthlyBalance.toInt() != 0 } // Partition into non-zero and zero lists
                .let { (nonZero, zero) -> nonZero + zero }

            SortOrder.ALPHABETICAL -> kategoriData.sortedBy {
                it.category.name.value
            }

            SortOrder.EXPENSES -> kategoriData.sortedByDescending {
                it.monthlyExpenses
            }
        }
    }

    private suspend fun createCategory(data: CreateCategoryData) {
        categoryCreator.createCategory(data) {
            loadCategories()
        }
    }

    override fun onEvent(event: KategoriSkriniEventi) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is KategoriSkriniEventi.OnReorder -> reorder(event.newOrder, event.sortOrder)
                is KategoriSkriniEventi.OnCreateCategory -> createCategory(event.createCategoryData)
                is KategoriSkriniEventi.OnReorderModalVisible -> {
                    reorderModalVisible.value = event.visible
                }

                is KategoriSkriniEventi.OnSortOrderModalVisible -> {
                    sortModalVisible.value = event.visible
                }

                is KategoriSkriniEventi.OnCategoryModalVisible -> {
                    categoryModalData.value = event.categoryModalData
                }
            }
        }
    }
}

suspend inline fun <T, R> Iterable<T>.mapAsync(
    scope: CoroutineScope,
    crossinline transform: suspend (T) -> R
): List<R> {
    return this.map {
        scope.async {
            transform(it)
        }
    }.awaitAll()
}
