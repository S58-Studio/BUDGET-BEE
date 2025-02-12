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
import com.oneSaver.navigation.ModifyScheduledSkrin
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

@Stable
@HiltViewModel
class ModifyPlannedVM @Inject constructor(
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
    private val transactionRepository: TransactionRepository
) : ComposeViewModel<ModifyPlannedSkrinState, ModifyPlannedSkrinEventi>() {

    private val transactionType = mutableStateOf(TransactionType.INCOME)
    private val startDate = mutableStateOf<LocalDateTime?>(null)
    private val intervalN = mutableStateOf<Int?>(null)
    private val intervalType = mutableStateOf<IntervalType?>(null)
    private val oneTime = mutableStateOf(false)
    private val initialTitle = mutableStateOf<String?>(null)
    private val description = mutableStateOf<String?>(null)
    private val account = mutableStateOf<Account?>(null)
    private val category = mutableStateOf<Category?>(null)
    private val amount = mutableDoubleStateOf(0.0)
    private val currency = mutableStateOf("")
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val categoryModalVisible = mutableStateOf(false)
    private val descriptionModalVisible = mutableStateOf(false)
    private val deleteTransactionModalVisible = mutableStateOf(false)
    private val transactionTypeModalVisible = mutableStateOf(false)
    private val amountModalVisible = mutableStateOf(false)
    private val recurringRuleModalData = mutableStateOf<RecurringRuleModalData?>(null)
    private val categoryModalData = mutableStateOf<CategoryModalData?>(null)
    private val accountModalData = mutableStateOf<AccountModalData?>(null)

    private var loadedRule: PlannedPaymentRule? = null
    private var editMode = false
    private var title: String? = null

    @Composable
    override fun uiState(): ModifyPlannedSkrinState {
        return ModifyPlannedSkrinState(
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
        return currency.value
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
    private fun getTransactionType(): TransactionType {
        return transactionType.value
    }

    @Composable
    private fun getStartDate(): LocalDateTime? {
        return startDate.value
    }

    @Composable
    private fun getIntervalN(): Int? {
        return intervalN.value
    }

    @Composable
    private fun getIntervalType(): IntervalType? {
        return intervalType.value
    }

    @Composable
    private fun getOneTime(): Boolean {
        return oneTime.value
    }

    @Composable
    private fun getInitialTitle(): String? {
        return initialTitle.value
    }

    @Composable
    private fun getDescription(): String? {
        return description.value
    }

    @Composable
    private fun getAccount(): Account? {
        return account.value
    }

    @Composable
    private fun getCategory(): Category? {
        return category.value
    }

    @Composable
    private fun getAmount(): Double {
        return amount.doubleValue
    }

    @Composable
    private fun getCategoryModalVisibility(): Boolean {
        return categoryModalVisible.value
    }

    @Composable
    private fun getDescriptionModalVisibility(): Boolean {
        return descriptionModalVisible.value
    }

    @Composable
    private fun getDeleteTransactionModalVisibility(): Boolean {
        return deleteTransactionModalVisible.value
    }

    @Composable
    private fun getTransactionTypeModalVisibility(): Boolean {
        return transactionTypeModalVisible.value
    }

    @Composable
    private fun getAmountModalVisibility(): Boolean {
        return amountModalVisible.value
    }

    @Composable
    private fun getCategoryModalData(): CategoryModalData? {
        return categoryModalData.value
    }

    @Composable
    private fun getAccountModalData(): AccountModalData? {
        return accountModalData.value
    }

    @Composable
    private fun getRecurringRuleModalData(): RecurringRuleModalData? {
        return recurringRuleModalData.value
    }

    override fun onEvent(event: ModifyPlannedSkrinEventi) {
        when (event) {
            is ModifyPlannedSkrinEventi.OnSave -> save()
            is ModifyPlannedSkrinEventi.OnDelete -> delete()
            is ModifyPlannedSkrinEventi.OnSetTransactionType ->
                updateTransactionType(event.newTransactionType)

            is ModifyPlannedSkrinEventi.OnDescriptionChanged ->
                updateDescription(event.newDescription)

            is ModifyPlannedSkrinEventi.OnCreateAccount -> createAccount(event.data)
            is ModifyPlannedSkrinEventi.OnCreateCategory -> createCategory(event.data)
            is ModifyPlannedSkrinEventi.OnAccountChanged -> updateAccount(event.newAccount)
            is ModifyPlannedSkrinEventi.OnAmountChanged -> updateAmount(event.newAmount)
            is ModifyPlannedSkrinEventi.OnTitleChanged -> updateTitle(event.newTitle)
            is ModifyPlannedSkrinEventi.OnRuleChanged ->
                updateRule(event.startDate, event.oneTime, event.intervalN, event.intervalType)

            is ModifyPlannedSkrinEventi.OnCategoryChanged -> updateCategory(event.newCategory)
            is ModifyPlannedSkrinEventi.OnModifyCategory -> editCategory(event.updatedCategory)
            is ModifyPlannedSkrinEventi.OnCategoryModalVisible ->
                categoryModalVisible.value = event.visible

            is ModifyPlannedSkrinEventi.OnCategoryModalDataChanged ->
                categoryModalData.value = event.categoryModalData

            is ModifyPlannedSkrinEventi.OnAccountModalDataChanged ->
                accountModalData.value = event.accountModalData

            is ModifyPlannedSkrinEventi.OnDescriptionModalVisible ->
                descriptionModalVisible.value = event.visible

            is ModifyPlannedSkrinEventi.OnTransactionTypeModalVisible ->
                transactionTypeModalVisible.value = event.visible

            is ModifyPlannedSkrinEventi.OnAmountModalVisible ->
                amountModalVisible.value = event.visible

            is ModifyPlannedSkrinEventi.OnDeleteTransactionModalVisible ->
                deleteTransactionModalVisible.value = event.visible

            is ModifyPlannedSkrinEventi.OnRecurringRuleModalDataChanged ->
                recurringRuleModalData.value = event.recurringRuleModalData
        }
    }

    fun start(screen: ModifyScheduledSkrin) {
        viewModelScope.launch {
            transactionType.value = screen.type
            editMode = screen.plannedPaymentRuleId != null

            val accounts = accountsAct(Unit)
            if (accounts.isEmpty()) {
                nav.back()
                return@launch
            }
            this@ModifyPlannedVM.accounts.value = accounts
            categories.value = categoryRepository.findAll().toImmutableList()

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

        transactionType.value = rule.type
        startDate.value = rule.startDate
        intervalN.value = rule.intervalN
        oneTime.value = rule.oneTime
        intervalType.value = rule.intervalType
        initialTitle.value = rule.title
        description.value = rule.description
        val selectedAccount = ioThread { accountDao.findById(rule.accountId)!!.toLegacyDomain() }
        account.value = selectedAccount
        category.value = rule.categoryId?.let {
            ioThread { categoryRepository.findById(CategoryId(it)) }
        }
        amount.doubleValue = rule.amount

        updateCurrency(account = selectedAccount)
    }

    private suspend fun updateCurrency(account: Account) {
        currency.value = account.currency ?: baseCurrency()
    }

    private suspend fun baseCurrency(): String = ioThread { settingsDao.findFirst().currency }

    private fun updateRule(
        startDate: LocalDateTime,
        oneTime: Boolean,
        intervalN: Int?,
        intervalType: IntervalType?
    ) {
        loadedRule = loadedRule().copy(
            startDate = startDate,
            intervalN = intervalN,
            intervalType = intervalType,
            oneTime = oneTime
        )
        this@ModifyPlannedVM.startDate.value = startDate
        this@ModifyPlannedVM.intervalN.value = intervalN
        this@ModifyPlannedVM.intervalType.value = intervalType
        this@ModifyPlannedVM.oneTime.value = oneTime

        saveIfEditMode()
    }

    private fun updateAmount(newAmount: Double) {
        loadedRule = loadedRule().copy(
            amount = newAmount
        )
        this@ModifyPlannedVM.amount.doubleValue = newAmount

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
        this@ModifyPlannedVM.description.value = newDescription

        saveIfEditMode()
    }

    private fun updateCategory(newCategory: Category?) {
        loadedRule = loadedRule().copy(
            categoryId = newCategory?.id?.value
        )
        this@ModifyPlannedVM.category.value = newCategory

        saveIfEditMode()
    }

    private fun updateAccount(newAccount: Account) {
        loadedRule = loadedRule().copy(
            accountId = newAccount.id
        )
        this@ModifyPlannedVM.account.value = newAccount

        viewModelScope.launch {
            updateCurrency(account = newAccount)
        }

        saveIfEditMode()
    }

    private fun updateTransactionType(newTransactionType: TransactionType) {
        loadedRule = loadedRule().copy(
            type = newTransactionType
        )
        this@ModifyPlannedVM.transactionType.value = newTransactionType

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
                        type = transactionType.value ?: error("no transaction type"),
                        startDate = startDate.value ?: error("no startDate"),
                        intervalN = intervalN.value ?: error("no intervalN"),
                        intervalType = intervalType.value ?: error("no intervalType"),
                        categoryId = category.value?.id?.value,
                        accountId = account.value?.id ?: error("no accountId"),
                        title = title?.trim(),
                        description = description.value?.trim(),
                        amount = amount.doubleValue ?: error("no amount"),

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
        if (transactionType.value == TransactionType.TRANSFER) {
            return false
        }

        if (amount.doubleValue == 0.0) {
            return false
        }

        return if (oneTime.value) validateOneTime() else validateRecurring()
    }

    private fun validateOneTime(): Boolean {
        return startDate.value != null
    }

    private fun validateRecurring(): Boolean {
        return startDate.value != null &&
                intervalN.value != null &&
                intervalN.value!! > 0 &&
                intervalType.value != null
    }

    private fun delete() {
        viewModelScope.launch {
            deleteTransactionModalVisible.value = false
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
                categories.value = categoryRepository.findAll().toImmutableList()

                updateCategory(it)
            }
        }
    }

    private fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            categoryCreator.editCategory(updatedCategory) {
                categories.value = categoryRepository.findAll().toImmutableList()
            }
        }
    }

    private fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            accountCreator.createAccount(data) {
                accounts.value = accountsAct(Unit)
            }
        }
    }

    private fun reset() {
        loadedRule = null

        initialTitle.value = null
        description.value = null
        category.value = null
    }

    private fun loadedRule() = loadedRule ?: error("Loaded transaction is null")
}
