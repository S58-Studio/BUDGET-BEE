package com.financeAndMoney.loans.mkopo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.financeAndMoney.base.legacy.SharedPrefs
import com.financeAndMoney.base.model.processByType
import com.financeAndMoney.data.database.dao.read.LoanRecordDao
import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.data.database.dao.write.WriteLoanDao
import com.financeAndMoney.data.model.LoanType
import com.financeAndMoney.frp.test.TestIdlingResource
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.legacy.domain.deprecated.logic.AccountCreator
import com.financeAndMoney.legacy.utils.format
import com.financeAndMoney.legacy.utils.getDefaultFIATCurrency
import com.financeAndMoney.legacy.utils.ioThread
import com.financeAndMoney.loans.mkopo.data.DisplayMkopoo
import com.financeAndMoney.userInterface.ComposeViewModel
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.AccountsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.loan.LoansAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.LoanCreator
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateAccountData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateLoanData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.LoanModalData
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
class MkopoVM @Inject constructor(
    private val loanRecordDao: LoanRecordDao,
    private val settingsDao: SettingsDao,
    private val loanCreator: LoanCreator,
    private val sharedPrefs: SharedPrefs,
    private val accountCreator: AccountCreator,
    private val loanTransactionsLogic: LoanTransactionsLogic,
    private val loansAct: LoansAct,
    private val accountsAct: AccountsAct,
    private val loanWriter: WriteLoanDao,
) : ComposeViewModel<MkopoSkrinState, MkopoScriniEventi>() {

    private val baseCurrencyCode = mutableStateOf(getDefaultFIATCurrency().currencyCode)
    private val loans = mutableStateOf<ImmutableList<DisplayMkopoo>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val selectedAccount = mutableStateOf<Account?>(null)
    private val loanModalData = mutableStateOf<LoanModalData?>(null)
    private val reorderModalVisible = mutableStateOf(false)

    /** If true paid off loans will be visible */
    private val paidOffLoanVisibility = mutableStateOf(true)

    /** Contains all loans including both paidOff and pending*/
    private var allLoans: ImmutableList<DisplayMkopoo> = persistentListOf()
    private var defaultCurrencyCode = ""
    private var totalOweAmount = 0.0
    private var totalOwedAmount = 0.0

    @Composable
    override fun uiState(): MkopoSkrinState {
        LaunchedEffect(Unit) {
            start()
        }

        return MkopoSkrinState(
            baseCurrency = getBaseCurrencyCode(),
            loans = getLoans(),
            accounts = getAccounts(),
            selectedAccount = getSelectedAccount(),
            loanModalData = getLoanModalData(),
            reorderModalVisible = getReorderModalVisible(),
            totalOweAmount = getTotalOweAmount(totalOweAmount, defaultCurrencyCode),
            totalOwedAmount = getTotalOwedAmount(totalOwedAmount, defaultCurrencyCode),
            paidOffLoanVisibility = getPaidOffLoanVisibility()
        )
    }

    @Composable
    private fun getReorderModalVisible() = reorderModalVisible.value

    @Composable
    private fun getLoanModalData() = loanModalData.value

    @Composable
    private fun getLoans(): ImmutableList<DisplayMkopoo> {
        return loans.value
    }

    @Composable
    private fun getBaseCurrencyCode(): String {
        return baseCurrencyCode.value
    }

    @Composable
    private fun getSelectedAccount() = selectedAccount.value

    @Composable
    private fun getAccounts() = accounts.value

    @Composable
    private fun getPaidOffLoanVisibility(): Boolean = paidOffLoanVisibility.value

    override fun onEvent(event: MkopoScriniEventi) {
        when (event) {
            is MkopoScriniEventi.OnLoanCreate -> {
                createLoan(event.createLoanData)
            }

            is MkopoScriniEventi.OnAddLoan -> {
                loanModalData.value = LoanModalData(
                    loan = null,
                    baseCurrency = baseCurrencyCode.value,
                    selectedAccount = selectedAccount.value
                )
            }

            is MkopoScriniEventi.OnLoanModalDismiss -> {
                loanModalData.value = null
            }

            is MkopoScriniEventi.OnReOrderModalShow -> {
                reorderModalVisible.value = event.show
            }

            is MkopoScriniEventi.OnReordered -> {
                reorder(event.reorderedList)
            }

            is MkopoScriniEventi.OnCreateAccount -> {
                createAccount(event.accountData)
            }

            MkopoScriniEventi.OnTogglePaidOffLoanVisibility -> {
                updatePaidOffLoanVisibility()
            }
        }
    }

    private fun start() {
        viewModelScope.launch(Dispatchers.Default) {
            TestIdlingResource.increment()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }.also {
                baseCurrencyCode.value = it
            }

            initialiseAccounts()

            totalOweAmount = 0.0
            totalOwedAmount = 0.0

            allLoans = ioThread {
                loansAct(Unit)
                    .map { loan ->
                        val (amountPaid, loanTotalAmount) = calculateAmountPaidAndTotalAmount(loan)
                        val percentPaid = if (loanTotalAmount != 0.0) {
                            amountPaid / loanTotalAmount
                        } else {
                            0.0
                        }
                        var currCode = findCurrencyCode(accounts.value, loan.accountId)

                        when (loan.type) {
                            LoanType.BORROW -> totalOweAmount += (loanTotalAmount - amountPaid)
                            LoanType.LEND -> totalOwedAmount += (loanTotalAmount - amountPaid)
                        }

                        DisplayMkopoo(
                            loan = loan,
                            loanTotalAmount = loanTotalAmount,
                            amountPaid = amountPaid,
                            currencyCode = currCode,
                            formattedDisplayText = "${amountPaid.format(currCode)} $currCode / ${
                                loanTotalAmount.format(
                                    currCode
                                )
                            } $currCode (${
                                percentPaid.times(
                                    100
                                ).format(2)
                            }%)",
                            percentPaid = percentPaid
                        )
                    }.toImmutableList()
            }
            filterLoans()

            TestIdlingResource.decrement()
        }
    }

    private fun getTotalOwedAmount(totalOwedAmount: Double, currCode: String): String {
        return if (totalOwedAmount != 0.0) {
            "${totalOwedAmount.format(currCode)} $currCode"
        } else {
            ""
        }
    }

    private fun getTotalOweAmount(totalOweAmount: Double, currCode: String): String {
        return if (totalOweAmount != 0.0) {
            "${totalOweAmount.format(currCode)} $currCode"
        } else {
            ""
        }
    }

    private suspend fun initialiseAccounts() {
        val accountsList = accountsAct(Unit)
        accounts.value = accountsList
        selectedAccount.value = defaultAccountId(accountsList)
        selectedAccount.value?.let {
            baseCurrencyCode.value = it.currency ?: defaultCurrencyCode
        }
    }

    private fun createLoan(data: CreateLoanData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val uuid = loanCreator.create(data) {
                start()
            }

            uuid?.let {
                loanTransactionsLogic.Loan.createAssociatedLoanTransaction(data = data, loanId = it)
            }

            TestIdlingResource.decrement()
        }
    }

    private fun reorder(newOrder: List<DisplayMkopoo>) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                newOrder.forEachIndexed { index, item ->
                    loanWriter.save(
                        item.loan.toEntity().copy(
                            orderNum = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
            start()

            TestIdlingResource.decrement()
        }
    }

    /** It filters [allLoans] and updates [loans] based on weather to show paid off loans or not */
    private fun filterLoans() {
        loans.value = when (paidOffLoanVisibility.value) {
            true -> allLoans
            false -> allLoans.filter { loan -> loan.percentPaid < 1.0 }.toImmutableList()
        }
    }

    private fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                accounts.value = accountsAct(Unit)
            }

            TestIdlingResource.decrement()
        }
    }

    private fun defaultAccountId(
        accounts: List<Account>,
    ): Account? {
        val lastSelectedId =
            sharedPrefs.getString(SharedPrefs.LAST_SELECTED_ACCOUNT_ID, null)?.let {
                UUID.fromString(it)
            }

        lastSelectedId?.let { uuid ->
            return accounts.find { it.id == uuid }
        } ?: run {
            return if (accounts.isNotEmpty()) accounts[0] else null
        }
    }

    private fun findCurrencyCode(accounts: List<Account>, accountId: UUID?): String {
        return accountId?.let {
            accounts.find { account -> account.id == it }?.currency
        } ?: defaultCurrencyCode
    }

    /**
     *  Calculates the total amount paid and the total loan amount including any changes made to the loan.
     *  @return A Pair containing the total amount paid and the total loan amount.
     */
    private suspend fun calculateAmountPaidAndTotalAmount(loan: Loan): Pair<Double, Double> {
        val loanRecords = ioThread { loanRecordDao.findAllByLoanId(loanId = loan.id) }
        val (amountPaid, loanTotalAmount) = loanRecords.fold(0.0 to loan.amount) { value, loanRecord ->
            val (currentAmountPaid, currentLoanTotalAmount) = value
            if (loanRecord.interest) return@fold value
            val convertedAmount = loanRecord.convertedAmount ?: loanRecord.amount

            loanRecord.loanRecordType.processByType(
                decreaseAction = { currentAmountPaid + convertedAmount to currentLoanTotalAmount },
                increaseAction = { currentAmountPaid to currentLoanTotalAmount + convertedAmount }
            )
        }
        return amountPaid to loanTotalAmount
    }

    private fun updatePaidOffLoanVisibility() {
        paidOffLoanVisibility.value = paidOffLoanVisibility.value.not()
        filterLoans()
    }
}
