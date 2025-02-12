package com.oneSaver.reportStatements

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.oneSaver.base.legacy.LegacyTransaction
import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.Expense
import com.oneSaver.data.model.Income
import com.oneSaver.data.model.Tag
import com.oneSaver.data.model.Transaction
import com.oneSaver.data.model.TransactionId
import com.oneSaver.data.model.Transfer
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.data.repository.TagRepository
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.data.repository.mapper.TransactionMapper
import com.oneSaver.data.temp.migration.getTransactionType
import com.oneSaver.data.temp.migration.getValue
import com.oneSaver.domains.RootScreen
import com.oneSaver.domains.usecase.csv.ExportCsvUseCase
import com.oneSaver.frp.filterSuspend
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.temp.toLegacy
import com.oneSaver.legacy.utils.getISOFormattedDateTime
import com.oneSaver.legacy.utils.scopedIOThread
import com.oneSaver.legacy.utils.timeNowUTC
import com.oneSaver.legacy.utils.toLowerCaseLocal
import com.oneSaver.legacy.utils.uiThread
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.domain.action.account.AccountsAct
import com.oneSaver.allStatus.domain.action.exchange.ExchangeAct
import com.oneSaver.allStatus.domain.action.settings.BaseCurrencyAct
import com.oneSaver.allStatus.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.oneSaver.allStatus.domain.action.transaction.TrnsWithDateDivsAct
import com.oneSaver.allStatus.domain.deprecated.logic.PlannedPaymentsLogic
import com.oneSaver.allStatus.domain.pure.data.IncomeExpenseTransferPair
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeData
import com.oneSaver.allStatus.domain.pure.transaction.trnCurrency
import com.oneSaver.allStatus.domain.pure.util.orZero
import com.oneSaver.allStatus.userInterface.theme.Gray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@Stable
@HiltViewModel
class StatementsVM @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val transactionRepository: TransactionRepository,
    private val ivyContext: MySaveCtx,
    private val exchangeAct: ExchangeAct,
    private val accountsAct: AccountsAct,
    private val categoryRepository: CategoryRepository,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val transactionMapper: TransactionMapper,
    private val tagRepository: TagRepository,
    private val exportCsvUseCase: ExportCsvUseCase,
) : ComposeViewModel<StatementSkrinState, StatementSkrinEventi>() {
    private val unSpecifiedCategory =
        Category(
            name = NotBlankTrimmedString.unsafe(stringRes(R.string.unspecified)),
            color = ColorInt(Gray.toArgb()),
            icon = null,
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
    private val baseCurrency = mutableStateOf("")
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val historyIncomeExpense = mutableStateOf(IncomeExpenseTransferPair.zero())
    private val filter = mutableStateOf<StatementsFilter?>(null)
    private val balance = mutableDoubleStateOf(0.0)
    private val income = mutableDoubleStateOf(0.0)
    private val expenses = mutableDoubleStateOf(0.0)
    private val upcomingIncome = mutableDoubleStateOf(0.0)
    private val upcomingExpenses = mutableDoubleStateOf(0.0)
    private val overdueIncome = mutableDoubleStateOf(0.0)
    private val overdueExpenses = mutableDoubleStateOf(0.0)
    private val history = mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())
    private val upcomingTransactions =
        mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private val overdueTransactions =
        mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val upcomingExpanded = mutableStateOf(false)
    private val overdueExpanded = mutableStateOf(false)
    private val loading = mutableStateOf(false)
    private val accountIdFilters = mutableStateOf<ImmutableList<UUID>>(persistentListOf())
    private val transactions = mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private val filterOverlayVisible = mutableStateOf(false)
    private val showTransfersAsIncExpCheckbox = mutableStateOf(false)
    private val treatTransfersAsIncExp = mutableStateOf(false)
    private val allTags = mutableStateOf<ImmutableList<Tag>>(persistentListOf())

    private var tagSearchJob: Job? = null
    private val tagSearchDebounceTimeInMills: Long = 500

    @Composable
    override fun uiState(): StatementSkrinState {
        LaunchedEffect(Unit) {
            start()
        }

        return StatementSkrinState(
            categories = categories.value,
            accounts = accounts.value,
            accountIdFilters = accountIdFilters.value,
            balance = balance.doubleValue,
            baseCurrency = baseCurrency.value,
            expenses = expenses.doubleValue,
            filter = filter.value,
            filterOverlayVisible = filterOverlayVisible.value,
            history = history.value,
            income = income.doubleValue,
            loading = loading.value,
            overdueExpanded = overdueExpanded.value,
            overdueExpenses = overdueExpenses.doubleValue,
            overdueIncome = overdueIncome.doubleValue,
            overdueTransactions = overdueTransactions.value,
            showTransfersAsIncExpCheckbox = showTransfersAsIncExpCheckbox.value,
            transactions = transactions.value,
            treatTransfersAsIncExp = treatTransfersAsIncExp.value,
            upcomingExpanded = upcomingExpanded.value,
            upcomingExpenses = upcomingExpenses.doubleValue,
            upcomingIncome = upcomingIncome.doubleValue,
            upcomingTransactions = upcomingTransactions.value,
            allTags = allTags.value
        )
    }

    override fun onEvent(event: StatementSkrinEventi) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is StatementSkrinEventi.OnFilter -> setFilter(event.filter)
                is StatementSkrinEventi.OnExport -> export(event.context)
                is StatementSkrinEventi.OnPayOrGet -> payOrGet(event.transaction)
                is StatementSkrinEventi.SkipTransaction -> skipTransaction(event.transaction)
                is StatementSkrinEventi.SkipTransactions -> skipTransactions(event.transactions)
                is StatementSkrinEventi.OnPayOrGetLegacy -> payOrGetLegacy(event.transaction)
                is StatementSkrinEventi.SkipTransactionLegacy -> skipTransactionLegacy(event.transaction)
                is StatementSkrinEventi.SkipTransactionsLegacy -> skipTransactionsLegacy(event.transactions)
                is StatementSkrinEventi.OnOverdueExpanded -> setOverdueExpanded(event.overdueExpanded)
                is StatementSkrinEventi.OnUpcomingExpanded -> setUpcomingExpanded(event.upcomingExpanded)
                is StatementSkrinEventi.OnFilterOverlayVisible -> setFilterOverlayVisible(event.filterOverlayVisible)
                is StatementSkrinEventi.OnTreatTransfersAsIncomeExpense -> onTreatTransfersAsIncomeExpense(
                    event.transfersAsIncomeExpense
                )

                is StatementSkrinEventi.OnTagSearch -> onTagSearch(event.data)
            }
        }
    }

    private suspend fun onTagSearch(query: String) {
        withContext(Dispatchers.IO) {
            tagSearchJob?.cancelAndJoin()
            delay(tagSearchDebounceTimeInMills) // Debounce effect
            tagSearchJob = launch(Dispatchers.IO) {
                NotBlankTrimmedString.from(query.toLowerCaseLocal())
                    .fold(
                        ifRight = {
                            allTags.value =
                                tagRepository.findByText(text = it.value).toImmutableList()
                        },
                        ifLeft = {
                            allTags.value = tagRepository.findAll().toImmutableList()
                        }
                    )
            }
        }
    }

    private fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            baseCurrency.value = baseCurrencyAct(Unit)
            accounts.value = accountsAct(Unit)
            categories.value =
                (listOf(unSpecifiedCategory) + categoryRepository.findAll()).toImmutableList()
            allTags.value = tagRepository.findAll().toImmutableList()
        }
    }

    private suspend fun setFilter(statementsFilter: StatementsFilter?) {
        scopedIOThread { scope ->
            if (statementsFilter == null) {
                // clear filter
                filter.value = null
                return@scopedIOThread
            }

            if (!statementsFilter.validate()) return@scopedIOThread
            val tempAccounts = statementsFilter.accounts
            val baseCurrency = baseCurrency.value
            filter.value = statementsFilter
            loading.value = true

            val transactionsList = filterTransactions(
                baseCurrency = baseCurrency,
                accounts = tempAccounts,
                filter = statementsFilter
            )

            val tempHistory = transactionsList
                .sortedByDescending { it.time }

            val historyWithDateDividers = scope.async {
                trnsWithDateDivsAct(
                    TrnsWithDateDivsAct.Input(
                        baseCurrency = baseCurrency,
                        transactions = tempHistory
                    )
                )
            }

            historyIncomeExpense.value = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = tempHistory,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )

            val tempIncome = historyIncomeExpense.value.income.toDouble() +
                    if (treatTransfersAsIncExp.value) historyIncomeExpense.value.transferIncome.toDouble() else 0.0

            val tempExpenses = historyIncomeExpense.value.expense.toDouble() +
                    if (treatTransfersAsIncExp.value) historyIncomeExpense.value.transferExpense.toDouble() else 0.0

            val tempBalance = calculateBalance(historyIncomeExpense.value).toDouble()

            val accountFilterIdList = scope.async { statementsFilter.accounts.map { it.id } }

            val timeNowUTC = timeNowUTC()

            // Upcoming
            val upcomingTransactionsList = transactionsList
                .filter {
                    !it.settled && it.time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                        .isAfter(timeNowUTC)
                }
                .sortedBy { it.time }
                .toImmutableList()

            val upcomingIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = upcomingTransactionsList,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )
            // Overdue
            val overdue = transactionsList.filter {
                !it.settled && it.time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                    .isBefore(timeNowUTC)
            }.sortedByDescending {
                it.time
            }.toImmutableList()
            val overdueIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = overdue,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )

            income.doubleValue = tempIncome
            expenses.doubleValue = tempExpenses
            upcomingExpenses.doubleValue = upcomingIncomeExpense.expense.toDouble()
            upcomingIncome.doubleValue = upcomingIncomeExpense.income.toDouble()
            overdueIncome.doubleValue = overdueIncomeExpense.income.toDouble()
            overdueExpenses.doubleValue = overdueIncomeExpense.expense.toDouble()
            history.value = historyWithDateDividers.await().toImmutableList()
            upcomingTransactions.value = upcomingTransactionsList.map {
                it.toLegacy(transactionMapper)
            }.toImmutableList()
            overdueTransactions.value = overdue.map {
                it.toLegacy(transactionMapper)
            }.toImmutableList()
            accounts.value = tempAccounts.toImmutableList()
            filter.value = statementsFilter
            loading.value = false
            accountIdFilters.value = accountFilterIdList.await().toImmutableList()
            transactions.value = transactionsList.map {
                it.toLegacy(transactionMapper)
            }.toImmutableList()
            balance.doubleValue = tempBalance
            filterOverlayVisible.value = false
            showTransfersAsIncExpCheckbox.value =
                statementsFilter.trnTypes.contains(TransactionType.TRANSFER)
        }
    }

    private suspend fun filterTransactions(
        baseCurrency: String,
        accounts: List<Account>,
        filter: StatementsFilter,
    ): ImmutableList<Transaction> {
        val filterAccountIds = filter.accounts.map { it.id }
        val filterCategoryIds =
            filter.categories.map { if (it.id.value == unSpecifiedCategory.id.value) null else it.id }
        val filterRange = filter.period?.toRange(ivyContext.startDayOfMonth)

        val transactions = if (filter.selectedTags.isNotEmpty()) {
            tagRepository.findByAllAssociatedIdForTagId(filter.selectedTags)
                .asSequence()
                .flatMap { it.value }
                .map { TransactionId(it.associatedId.value) }
                .distinct()
                .toList()
                .let {
                    transactionRepository.findByIds(it)
                }
        } else {
            transactionRepository.findAll()
        }

        return transactions
            .filter {
                with(transactionMapper) {
                    filter.trnTypes.contains(it.getTransactionType())
                }
            }
            .filter {
                // Filter by Time Period

                filterRange ?: return@filter false

                filterRange.includes(it.time.atZone(ZoneId.systemDefault()).toLocalDateTime())
            }
            .filter { trn ->
                // Filter by Accounts
                when (trn) {
                    is Transfer -> {
                        filterAccountIds.contains(trn.fromAccount.value) || // Transfers Out
                                (filterAccountIds.contains(trn.toAccount.value)) // Transfers In
                    }

                    is Expense -> {
                        filterAccountIds.contains(trn.account.value)
                    }

                    is Income -> {
                        filterAccountIds.contains(trn.account.value)
                    }
                }
            }
            .filter { trn ->
                // Filter by Categories

                filterCategoryIds.contains(trn.category) || with(transactionMapper) {
                    (trn.getTransactionType() == TransactionType.TRANSFER)
                }
            }
            .filterSuspend {
                // Filter by Amount
                // !NOTE: Amount must be converted to baseCurrency amount

                val trnAmountBaseCurrency = exchangeAct(
                    ExchangeAct.Input(
                        data = ExchangeData(
                            baseCurrency = baseCurrency,
                            fromCurrency = trnCurrency(it, accounts, baseCurrency),
                        ),
                        amount = it.getValue()
                    )
                ).orZero().toDouble()

                (filter.minAmount == null || trnAmountBaseCurrency >= filter.minAmount) &&
                        (filter.maxAmount == null || trnAmountBaseCurrency <= filter.maxAmount)
            }
            .filter {
                // Filter by Included Keywords

                val includeKeywords = filter.includeKeywords
                if (includeKeywords.isEmpty()) return@filter true

                it.title?.let { title ->
                    includeKeywords.forEach { keyword ->
                        if (title.value.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                it.description?.let { description ->
                    includeKeywords.forEach { keyword ->
                        if (description.value.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                false
            }
            .filter {
                // Filter by Excluded Keywords

                val excludedKeywords = filter.excludeKeywords
                if (excludedKeywords.isEmpty()) return@filter true

                it.title?.let { title ->
                    excludedKeywords.forEach { keyword ->
                        if (title.value.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }
                it.description?.let { description ->
                    excludedKeywords.forEach { keyword ->
                        if (description.value.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }
                true
            }.toImmutableList()
    }

    private fun String.containsLowercase(anotherString: String): Boolean {
        return this.toLowerCaseLocal().contains(anotherString.toLowerCaseLocal())
    }

    private fun calculateBalance(incomeExpenseTransferPair: IncomeExpenseTransferPair): BigDecimal {
        return incomeExpenseTransferPair.income + incomeExpenseTransferPair.transferIncome - incomeExpenseTransferPair.expense - incomeExpenseTransferPair.transferExpense
    }

    private suspend fun export(context: Context) {
        val filter = filter.value ?: return
        if (!filter.validate()) return

        ivyContext.createNewFile(
            "MySaveReport-${
                timeNowUTC().getISOFormattedDateTime()
            }.csv"
        ) { fileUri ->
            viewModelScope.launch {
                loading.value = true

                exportCsvUseCase.exportToFile(
                    outputFile = fileUri,
                    exportScope = {
                        filterTransactions(
                            baseCurrency = baseCurrency.value,
                            accounts = accounts.value,
                            filter = filter
                        )
                    }
                )

                (context as RootScreen).shareCSVFile(
                    fileUri = fileUri
                )

                loading.value = false
            }
        }
    }

    private fun setUpcomingExpanded(expanded: Boolean) {
        upcomingExpanded.value = expanded
    }

    private fun setOverdueExpanded(expanded: Boolean) {
        overdueExpanded.value = expanded
    }

    private suspend fun payOrGet(transaction: Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGet(
                transaction = transaction
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }

    @Deprecated("Uses legacy Transaction")
    private suspend fun payOrGetLegacy(transaction: com.oneSaver.base.legacy.Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(transaction = transaction) {
                start()
                setFilter(filter.value)
            }
        }
    }

    private fun setFilterOverlayVisible(visible: Boolean) {
        filterOverlayVisible.value = visible
    }

    private fun onTreatTransfersAsIncomeExpense(transfersAsIncExp: Boolean) {
        income.doubleValue = historyIncomeExpense.value.income.toDouble() +
                if (transfersAsIncExp) historyIncomeExpense.value.transferIncome.toDouble() else 0.0
        expenses.doubleValue = historyIncomeExpense.value.expense.toDouble() +
                if (transfersAsIncExp) historyIncomeExpense.value.transferExpense.toDouble() else 0.0
        treatTransfersAsIncExp.value = transfersAsIncExp
    }

    private suspend fun skipTransaction(transaction: Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGet(
                transaction = transaction,
                skipTransaction = true
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }

    @Deprecated("Uses legacy Transaction")
    private suspend fun skipTransactionLegacy(transaction: com.oneSaver.base.legacy.Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(
                transaction = transaction,
                skipTransaction = true
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }

    private suspend fun skipTransactions(transactions: List<Transaction>) {
        uiThread {
            plannedPaymentsLogic.payOrGet(
                transactions = transactions,
                skipTransaction = true
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }

    @Deprecated("Uses legacy Transaction")
    private suspend fun skipTransactionsLegacy(transactions: List<com.oneSaver.base.legacy.Transaction>) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(
                transactions = transactions,
                skipTransaction = true
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }
}