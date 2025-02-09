package com.financeAndMoney.iliyopangwa.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.userInterface.ComposeViewModel
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.data.repository.CategoryRepository
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.PlannedPaymentRule
import com.financeAndMoney.legacy.utils.ioThread
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.AccountsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.PlannedPaymentsLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class ScheduledPaymntsKard @Inject constructor(
    private val settingsDao: SettingsDao,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val categoriesRepository: CategoryRepository,
    private val accountsAct: AccountsAct
) : ComposeViewModel<ScheduledPaymntSkrinState, ScheduledPaymntsSkrinEvent>() {

    private val currency = mutableStateOf("")
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val oneTimePlannedPayment =
        mutableStateOf<ImmutableList<PlannedPaymentRule>>(persistentListOf())
    private val recurringPlannedPayment =
        mutableStateOf<ImmutableList<PlannedPaymentRule>>(persistentListOf())
    private val oneTimeIncome = mutableDoubleStateOf(0.0)
    private val oneTimeExpenses = mutableDoubleStateOf(0.0)
    private val recurringIncome = mutableDoubleStateOf(0.0)
    private val recurringExpenses = mutableDoubleStateOf(0.0)
    private val isOneTimePaymentsExpanded = mutableStateOf(true)
    private val isRecurringPaymentsExpanded = mutableStateOf(true)

    @Composable
    override fun uiState(): ScheduledPaymntSkrinState {
        LaunchedEffect(Unit) {
            start()
        }

        return ScheduledPaymntSkrinState(
            currency = getCurrency(),
            categories = getCategories(),
            accounts = getAccounts(),
            oneTimeIncome = getOneTimeIncome(),
            oneTimeExpenses = getOneTimeExpenses(),
            recurringExpenses = getRecurringExpenses(),
            recurringIncome = getRecurringIncome(),
            recurringPlannedPayment = getRecurringPlannedPayment(),
            oneTimePlannedPayment = getOneTimePlannedPayment(),
            isOneTimePaymentsExpanded = getOneTimePaymentsExpanded(),
            isRecurringPaymentsExpanded = getRecurringPaymentsExpanded()
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
    private fun getOneTimePlannedPayment(): ImmutableList<PlannedPaymentRule> {
        return oneTimePlannedPayment.value
    }

    @Composable
    private fun getRecurringPlannedPayment(): ImmutableList<PlannedPaymentRule> {
        return recurringPlannedPayment.value
    }

    @Composable
    private fun getOneTimeExpenses(): Double {
        return oneTimeExpenses.doubleValue
    }

    @Composable
    private fun getOneTimeIncome(): Double {
        return oneTimeIncome.doubleValue
    }

    @Composable
    private fun getRecurringExpenses(): Double {
        return recurringExpenses.doubleValue
    }

    @Composable
    private fun getRecurringIncome(): Double {
        return recurringIncome.doubleValue
    }

    @Composable
    private fun getRecurringPaymentsExpanded(): Boolean {
        return isRecurringPaymentsExpanded.value
    }

    @Composable
    private fun getOneTimePaymentsExpanded(): Boolean {
        return isOneTimePaymentsExpanded.value
    }

    override fun onEvent(event: ScheduledPaymntsSkrinEvent) {
        when (event) {
            is ScheduledPaymntsSkrinEvent.OnOneTimePaymentsExpanded -> {
                isOneTimePaymentsExpanded.value = event.isExpanded
            }
            is ScheduledPaymntsSkrinEvent.OnRecurringPaymentsExpanded -> {
                isRecurringPaymentsExpanded.value = event.isExpanded
            }
        }
    }

    private fun start() {
        viewModelScope.launch {
            val settings = ioThread { settingsDao.findFirst() }
            currency.value = settings.currency

            categories.value = categoriesRepository.findAll().toImmutableList()
            accounts.value = accountsAct(Unit)

            oneTimePlannedPayment.value =
                ioThread { plannedPaymentsLogic.oneTime() }.toImmutableList()
            oneTimeIncome.doubleValue = ioThread { plannedPaymentsLogic.oneTimeIncome() }
            oneTimeExpenses.doubleValue = ioThread { plannedPaymentsLogic.oneTimeExpenses() }

            recurringPlannedPayment.value =
                ioThread { plannedPaymentsLogic.recurring() }.toImmutableList()
            recurringIncome.doubleValue = ioThread { plannedPaymentsLogic.recurringIncome() }
            recurringExpenses.doubleValue = ioThread { plannedPaymentsLogic.recurringExpenses() }
        }
    }
}
