package com.oneSaver.iliyopangwa.editUpcomingPayments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.data.database.dao.read.PlannedPaymentRuleDao
import com.oneSaver.data.database.dao.read.SettingsDao
import com.oneSaver.data.database.dao.write.WritePlannedPaymentRuleDao
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.IntervalType
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.PlannedPaymentRule
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.legacy.domain.deprecated.logic.AccountCreator
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.navigation.EditPlannedScreen
import com.oneSaver.navigation.Navigation
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.allStatus.domain.action.account.AccountsAct
import com.oneSaver.allStatus.domain.deprecated.logic.CategoryCreator
import com.oneSaver.allStatus.domain.deprecated.logic.PlannedPaymentsGenerator
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import com.oneSaver.allStatus.userInterface.theme.modal.RecurringRuleModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AccountModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.oneSaver.base.time.TimeConverter

@Stable
@HiltViewModel
class EditPlannedViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryRepository: CategoryRepository,
    private val settingsDao: SettingsDao,
    private val nav: Navigation,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val plannedPaymentsGenerator: PlannedPaymentsGenerator,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val accountsAct: AccountsAct,
    private val plannedPaymentRuleWriter: WritePlannedPaymentRuleDao,
    private val transactionRepository: TransactionRepository,
    private val timeConverter: TimeConverter,
) : ComposeViewModel<EditPlannedScreenState, EditPlannedScreenEvents>() {

    private var transactionType by mutableStateOf(TransactionType.INCOME)
    private var startDate by mutableStateOf<LocalDateTime?>(null)
    private var intervalN by mutableStateOf<Int?>(null)
    private var intervalType by mutableStateOf<IntervalType?>(null)
    private var oneTime by mutableStateOf(false)
    private var initialTitle by mutableStateOf<String?>(null)
    private var description by mutableStateOf<String?>(null)
    private var account by mutableStateOf<Account?>(null)
    private var category by mutableStateOf<Category?>(null)
    private var amount by mutableDoubleStateOf(0.0)
    private var currency by mutableStateOf("")
    private var categories by mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private var accounts by mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private var categoryModalVisible by mutableStateOf(false)
    private var descriptionModalVisible by mutableStateOf(false)
    private var deleteTransactionModalVisible by mutableStateOf(false)
    private var transactionTypeModalVisible by mutableStateOf(false)
    private var amountModalVisible by mutableStateOf(false)
    private var recurringRuleModalData by mutableStateOf<RecurringRuleModalData?>(null)
    private var categoryModalData by mutableStateOf<CategoryModalData?>(null)
    private var accountModalData by mutableStateOf<AccountModalData?>(null)

    private var loadedRule: PlannedPaymentRule? = null
    private var editMode = false
    private var title: String? = null

    @Composable
    override fun uiState(): EditPlannedScreenState {
        return EditPlannedScreenState(
            currency = getCurrency(),
            categories = getCategories(),
            accounts = getAccounts(),
            transactionType = getTransactionType(),
            startDate = getStartDate(),
            intervalN = getIntervalN(),
            oneTime = getOneTime(),
            account = getAccount(),
            category = getCategory(),
            amount = getAmount(),
            initialTitle = getInitialTitle(),
            description = getDescription(),
            intervalType = getIntervalType(),
            categoryModalVisible = getCategoryModalVisibility(),
            categoryModalData = getCategoryModalData(),
            accountModalData = getAccountModalData(),
            deleteTransactionModalVisible = getDeleteTransactionModalVisibility(),
            descriptionModalVisible = getDescriptionModalVisibility(),
            amountModalVisible = getAmountModalVisibility(),
            transactionTypeModalVisible = getTransactionTypeModalVisibility(),
            recurringRuleModalData = getRecurringRuleModalData()
        )
    }

    @Composable
    private fun getCurrency(): String {
        return currency
    }

    @Composable
    private fun getCategories(): ImmutableList<Category> {
        return categories
    }

    @Composable
    private fun getAccounts(): ImmutableList<Account> {
        return accounts
    }

    @Composable
    private fun getTransactionType(): TransactionType {
        return transactionType
    }

    @Composable
    private fun getStartDate(): LocalDateTime? {
        return startDate
    }

    @Composable
    private fun getIntervalN(): Int? {
        return intervalN
    }

    @Composable
    private fun getIntervalType(): IntervalType? {
        return intervalType
    }

    @Composable
    private fun getOneTime(): Boolean {
        return oneTime
    }

    @Composable
    private fun getInitialTitle(): String? {
        return initialTitle
    }

    @Composable
    private fun getDescription(): String? {
        return description
    }

    @Composable
    private fun getAccount(): Account? {
        return account
    }

    @Composable
    private fun getCategory(): Category? {
        return category
    }

    @Composable
    private fun getAmount(): Double {
        return amount
    }

    @Composable
    private fun getCategoryModalVisibility(): Boolean {
        return categoryModalVisible
    }

    @Composable
    private fun getDescriptionModalVisibility(): Boolean {
        return descriptionModalVisible
    }

    @Composable
    private fun getDeleteTransactionModalVisibility(): Boolean {
        return deleteTransactionModalVisible
    }

    @Composable
    private fun getTransactionTypeModalVisibility(): Boolean {
        return transactionTypeModalVisible
    }

    @Composable
    private fun getAmountModalVisibility(): Boolean {
        return amountModalVisible
    }

    @Composable
    private fun getCategoryModalData(): CategoryModalData? {
        return categoryModalData
    }

    @Composable
    private fun getAccountModalData(): AccountModalData? {
        return accountModalData
    }

    @Composable
    private fun getRecurringRuleModalData(): RecurringRuleModalData? {
        return recurringRuleModalData
    }

    override fun onEvent(event: EditPlannedScreenEvents) {
        when (event) {
            is EditPlannedScreenEvents.OnSave -> save()
            is EditPlannedScreenEvents.OnDelete -> delete()
            is EditPlannedScreenEvents.OnSetTransactionType ->
                updateTransactionType(event.newTransactionType)

            is EditPlannedScreenEvents.OnDescriptionChanged ->
                updateDescription(event.newDescription)

            is EditPlannedScreenEvents.OnCreateAccount -> createAccount(event.data)
            is EditPlannedScreenEvents.OnCreateCategory -> createCategory(event.data)
            is EditPlannedScreenEvents.OnAccountChanged -> updateAccount(event.newAccount)
            is EditPlannedScreenEvents.OnAmountChanged -> updateAmount(event.newAmount)
            is EditPlannedScreenEvents.OnTitleChanged -> updateTitle(event.newTitle)
            is EditPlannedScreenEvents.OnRuleChanged ->
                updateRule(event.startDate, event.oneTime, event.intervalN, event.intervalType)

            is EditPlannedScreenEvents.OnCategoryChanged -> updateCategory(event.newCategory)
            is EditPlannedScreenEvents.OnEditCategory -> editCategory(event.updatedCategory)
            is EditPlannedScreenEvents.OnCategoryModalVisible ->
                categoryModalVisible = event.visible

            is EditPlannedScreenEvents.OnCategoryModalDataChanged ->
                categoryModalData = event.categoryModalData

            is EditPlannedScreenEvents.OnAccountModalDataChanged ->
                accountModalData = event.accountModalData

            is EditPlannedScreenEvents.OnDescriptionModalVisible ->
                descriptionModalVisible = event.visible

            is EditPlannedScreenEvents.OnTransactionTypeModalVisible ->
                transactionTypeModalVisible = event.visible

            is EditPlannedScreenEvents.OnAmountModalVisible ->
                amountModalVisible = event.visible

            is EditPlannedScreenEvents.OnDeleteTransactionModalVisible ->
                deleteTransactionModalVisible = event.visible

            is EditPlannedScreenEvents.OnRecurringRuleModalDataChanged ->
                recurringRuleModalData = event.recurringRuleModalData
        }
    }

    fun start(screen: EditPlannedScreen) {
        viewModelScope.launch {
            transactionType = screen.type
            editMode = screen.plannedPaymentRuleId != null

            val accounts = accountsAct(Unit)
            if (accounts.isEmpty()) {
                nav.back()
                return@launch
            }
            this@EditPlannedViewModel.accounts = accounts
            categories = categoryRepository.findAll().toImmutableList()

            reset()

            loadedRule = screen.plannedPaymentRuleId?.let {
                ioThread { plannedPaymentRuleDao.findById(it)!!.toLegacyDomain() }
            } ?: PlannedPaymentRule(
                startDate = null,
                intervalN = null,
                intervalType = null,
                oneTime = false,
                type = screen.type,
                amount = screen.amount ?: 0.0,
                accountId = screen.accountId ?: accounts.first().id,
                categoryId = screen.categoryId,
                title = screen.title,
                description = screen.description
            )

            display(loadedRule!!)
        }
    }

    private suspend fun display(rule: PlannedPaymentRule) {
        this.title = rule.title

        transactionType = rule.type
        startDate = with(timeConverter) { rule.startDate?.toLocalDateTime() }
        intervalN = rule.intervalN
        oneTime = rule.oneTime
        intervalType = rule.intervalType
        initialTitle = rule.title
        description = rule.description
        val selectedAccount = ioThread { accountDao.findById(rule.accountId)!!.toLegacyDomain() }
        account = selectedAccount
        category = rule.categoryId?.let {
            ioThread { categoryRepository.findById(CategoryId(it)) }
        }
        amount = rule.amount

        updateCurrency(account = selectedAccount)
    }

    private suspend fun updateCurrency(account: Account) {
        currency = account.currency ?: baseCurrency()
    }

    private suspend fun baseCurrency(): String = ioThread { settingsDao.findFirst().currency }

    private fun updateRule(
        startDate: LocalDateTime,
        oneTime: Boolean,
        intervalN: Int?,
        intervalType: IntervalType?
    ) {
        loadedRule = loadedRule().copy(
            startDate = with(timeConverter) { startDate.toUTC() },
            intervalN = intervalN,
            intervalType = intervalType,
            oneTime = oneTime
        )
        this@EditPlannedViewModel.startDate = startDate
        this@EditPlannedViewModel.intervalN = intervalN
        this@EditPlannedViewModel.intervalType = intervalType
        this@EditPlannedViewModel.oneTime = oneTime

        saveIfEditMode()
    }

    private fun updateAmount(newAmount: Double) {
        loadedRule = loadedRule().copy(
            amount = newAmount
        )
        this@EditPlannedViewModel.amount = newAmount

        saveIfEditMode()
    }

    private fun updateTitle(newTitle: String?) {
        loadedRule = loadedRule().copy(
            title = newTitle
        )
        this.title = newTitle

        saveIfEditMode()
    }

    private fun updateDescription(newDescription: String?) {
        loadedRule = loadedRule().copy(
            description = newDescription
        )
        this@EditPlannedViewModel.description = newDescription

        saveIfEditMode()
    }

    private fun updateCategory(newCategory: Category?) {
        loadedRule = loadedRule().copy(
            categoryId = newCategory?.id?.value
        )
        this@EditPlannedViewModel.category = newCategory

        saveIfEditMode()
    }

    private fun updateAccount(newAccount: Account) {
        loadedRule = loadedRule().copy(
            accountId = newAccount.id
        )
        this@EditPlannedViewModel.account = newAccount

        viewModelScope.launch {
            updateCurrency(account = newAccount)
        }

        saveIfEditMode()
    }

    private fun updateTransactionType(newTransactionType: TransactionType) {
        loadedRule = loadedRule().copy(
            type = newTransactionType
        )
        this@EditPlannedViewModel.transactionType = newTransactionType

        saveIfEditMode()
    }

    private fun saveIfEditMode() {
        if (editMode) {
            save(false)
        }
    }

    private fun save(closeScreen: Boolean = true) {
        if (!validate()) {
            return
        }

        viewModelScope.launch {
            try {
                ioThread {
                    loadedRule = loadedRule().copy(
                        type = transactionType ?: error("no transaction type"),
                        startDate = with(timeConverter) { startDate?.toUTC() }
                            ?: error("no startDate"),
                        intervalN = intervalN ?: error("no intervalN"),
                        intervalType = intervalType ?: error("no intervalType"),
                        categoryId = category?.id?.value,
                        accountId = account?.id ?: error("no accountId"),
                        title = title?.trim(),
                        description = description?.trim(),
                        amount = amount ?: error("no amount"),

                        isSynced = false
                    )

                    plannedPaymentRuleWriter.save(loadedRule().toEntity())
                    plannedPaymentsGenerator.generate(loadedRule())
                }

                if (closeScreen) {
                    nav.back()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validate(): Boolean {
        if (transactionType == TransactionType.TRANSFER) {
            return false
        }

        if (amount == 0.0) {
            return false
        }

        return if (oneTime) validateOneTime() else validateRecurring()
    }

    private fun validateOneTime(): Boolean {
        return startDate != null
    }

    private fun validateRecurring(): Boolean {
        return startDate != null &&
                intervalN != null &&
                intervalN!! > 0 &&
                intervalType != null
    }

    private fun delete() {
        viewModelScope.launch {
            deleteTransactionModalVisible = false
            ioThread {
                loadedRule?.let {
                    plannedPaymentRuleWriter.deleteById(it.id)
                    transactionRepository.deletedByRecurringRuleIdAndNoDateTime(
                        recurringRuleId = it.id
                    )
                }
                nav.back()
            }
        }
    }

    private fun createCategory(data: CreateCategoryData) {
        viewModelScope.launch {
            categoryCreator.createCategory(data) {
                categories = categoryRepository.findAll().toImmutableList()

                updateCategory(it)
            }
        }
    }

    private fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            categoryCreator.editCategory(updatedCategory) {
                categories = categoryRepository.findAll().toImmutableList()
            }
        }
    }

    private fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            accountCreator.createAccount(data) {
                accounts = accountsAct(Unit)
            }
        }
    }

    private fun reset() {
        loadedRule = null

        initialTitle = null
        description = null
        category = null
    }

    private fun loadedRule() = loadedRule ?: error("Loaded transaction is null")
}
