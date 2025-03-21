package com.oneSaver.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.frp.action.thenMap
import com.oneSaver.legacy.frp.thenInvokeAfter
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.allStatus.domain.action.account.AccountsAct
import com.oneSaver.allStatus.domain.action.category.LegacyCategoryIncomeWithAccountFiltersAct
import com.oneSaver.allStatus.domain.action.settings.BaseCurrencyAct
import com.oneSaver.allStatus.domain.action.transaction.TrnsWithRangeAndAccFiltersAct
import com.oneSaver.allStatus.domain.data.SortOrder
import com.oneSaver.allStatus.domain.deprecated.logic.CategoryCreator
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModalData
import com.oneSaver.base.time.TimeConverter
import com.oneSaver.base.time.TimeProvider
import com.oneSaver.domains.features.Features
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
    private val ivyContext: com.oneSaver.legacy.MySaveCtx,
    private val sharedPrefs: SharedPrefs,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val accountsAct: AccountsAct,
    private val trnsWithRangeAndAccFiltersAct: TrnsWithRangeAndAccFiltersAct,
    private val categoryIncomeWithAccountFiltersAct: LegacyCategoryIncomeWithAccountFiltersAct,
    private val features: Features,
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
) : ComposeViewModel<KategoriSkriniState, KategoriSkriniEventi>() {

    private val baseCurrency = mutableStateOf("")
    private val categories =
        mutableStateOf<ImmutableList<KategoriData>>(persistentListOf<KategoriData>())
    private val searchQuery = mutableStateOf("")
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
            sortModalVisible = getSortModalVisible(),
            compactCategoriesModeEnabled = getCompactCategoriesMode(),
            showCategorySearchBar = getShowCategorySearchBar()
        )
    }

    @Composable
    private fun getCompactCategoriesMode(): Boolean {
        return features.compactCategoriesMode.asEnabledState()
    }

    @Composable
    private fun getShowCategorySearchBar(): Boolean {
        return features.showCategorySearchBar.asEnabledState()
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency.value
    }

    @Composable
    private fun getCategories(): ImmutableList<KategoriData> {
        val allCats = categories.value
        return remember(allCats, searchQuery.value) {
            allCats.filter {
                searchQuery.value.lowercase().trim() in it.category.name.toString().lowercase()
            }.toImmutableList()
        }
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
            ).toRange(
                ivyContext.startDayOfMonth,
                timeConverter,
                timeProvider
            ) // this must be monthly

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
        com.oneSaver.legacy.utils.scopedIOThread { scope ->
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

    private fun updateSearchQuery(queryString: String) {
        searchQuery.value = queryString
    }

    private suspend fun reorder(
        newOrder: List<KategoriData>,
        sortOrder: SortOrder = SortOrder.DEFAULT
    ) {
        val sortedList = sortList(newOrder, sortOrder).toImmutableList()

        if (sortOrder == SortOrder.DEFAULT) {
            ioThread {
                sortedList.forEachIndexed { index, KategoriData ->
                    categoryRepository.save(KategoriData.category.copy(orderNum = index.toDouble()))
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
        KategoriData: List<KategoriData>,
        sortOrder: SortOrder
    ): List<KategoriData> {
        return when (sortOrder) {
            SortOrder.DEFAULT -> KategoriData.sortedBy {
                it.category.orderNum
            }

            SortOrder.BALANCE_AMOUNT -> KategoriData.sortedByDescending {
                it.monthlyBalance
            }.partition { it.monthlyBalance.toInt() != 0 } // Partition into non-zero and zero lists
                .let { (nonZero, zero) -> nonZero + zero }

            SortOrder.ALPHABETICAL -> KategoriData.sortedBy {
                it.category.name.value
            }

            SortOrder.EXPENSES -> KategoriData.sortedByDescending {
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

                is KategoriSkriniEventi.OnSearchQueryUpdate -> updateSearchQuery(event.queryString)
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