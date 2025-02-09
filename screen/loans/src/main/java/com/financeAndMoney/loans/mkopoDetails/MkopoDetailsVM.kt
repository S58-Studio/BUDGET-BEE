package com.financeAndMoney.loans.mkopoDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.base.model.LoanRecordType
import com.financeAndMoney.data.database.dao.read.LoanRecordDao
import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.data.repository.TransactionRepository
import com.financeAndMoney.data.repository.mapper.TransactionMapper
import com.financeAndMoney.frp.test.TestIdlingResource
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.legacy.datamodel.LoanRecord
import com.financeAndMoney.legacy.datamodel.temp.toLegacy
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import com.financeAndMoney.legacy.domain.deprecated.logic.AccountCreator
import com.financeAndMoney.legacy.utils.computationThread
import com.financeAndMoney.legacy.utils.ioThread
import com.financeAndMoney.loans.mkopo.data.DisplayMkopoRekodi
import com.financeAndMoney.loans.mkopoDetails.events.DeleteMkopoModalEvent
import com.financeAndMoney.loans.mkopoDetails.events.MkopoDetailsScreenEvent
import com.financeAndMoney.loans.mkopoDetails.events.MkopoModalEvent
import com.financeAndMoney.loans.mkopoDetails.events.MkopoRecordModalEvent
import com.financeAndMoney.navigation.MkopoDetailsSkrin
import com.financeAndMoney.navigation.Navigation
import com.financeAndMoney.userInterface.ComposeViewModel
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.AccountsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.loan.LoanByIdAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.LoanCreator
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.LoanRecordCreator
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateAccountData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateLoanRecordData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.EditLoanRecordData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.LoanModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.LoanRecordModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@Stable
@HiltViewModel
class MkopoDetailsVM @Inject constructor(
    private val loanRecordDao: LoanRecordDao,
    private val loanCreator: LoanCreator,
    private val loanRecordCreator: LoanRecordCreator,
    private val settingsDao: SettingsDao,
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
    private val accountCreator: AccountCreator,
    private val loanTransactionsLogic: LoanTransactionsLogic,
    private val nav: Navigation,
    private val accountsAct: AccountsAct,
    private val loanByIdAct: LoanByIdAct,
) : ComposeViewModel<MkopoDetailsScreenState, MkopoDetailsScreenEvent>() {

    private val baseCurrency = mutableStateOf("")
    private val loan = mutableStateOf<Loan?>(null)
    private val displayLoanRecords =
        mutableStateOf<ImmutableList<DisplayMkopoRekodi>>(persistentListOf())
    private val loanTotalAmount = mutableDoubleStateOf(0.0)
    private val amountPaid = mutableDoubleStateOf(0.0)
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val loanInterestAmountPaid = mutableDoubleStateOf(0.0)
    private val selectedLoanAccount = mutableStateOf<Account?>(null)
    private var associatedTransaction: Transaction? = null
    private val createLoanTransaction = mutableStateOf(false)
    private var defaultCurrencyCode = ""
    private val loanModalData = mutableStateOf<LoanModalData?>(null)
    private val loanRecordModalData = mutableStateOf<LoanRecordModalData?>(null)
    private val waitModalVisible = mutableStateOf(false)
    private val isDeleteModalVisible = mutableStateOf(false)
    lateinit var screen: MkopoDetailsSkrin

    @Composable
    override fun uiState(): MkopoDetailsScreenState {
        LaunchedEffect(Unit) {
            start()
        }

        return MkopoDetailsScreenState(
            baseCurrency = baseCurrency.value,
            loan = loan.value,
            displayMkopoRekodis = displayLoanRecords.value,
            loanTotalAmount = loanTotalAmount.doubleValue,
            amountPaid = amountPaid.doubleValue,
            loanAmountPaid = loanInterestAmountPaid.doubleValue,
            accounts = accounts.value,
            selectedLoanAccount = selectedLoanAccount.value,
            createLoanTransaction = createLoanTransaction.value,
            loanModalData = loanModalData.value,
            loanRecordModalData = loanRecordModalData.value,
            waitModalVisible = waitModalVisible.value,
            isDeleteModalVisible = isDeleteModalVisible.value
        )
    }

    override fun onEvent(event: MkopoDetailsScreenEvent) {
        when (event) {
            is MkopoRecordModalEvent -> handleLoanRecordModalEvents(event)
            is MkopoModalEvent -> handleLoanModalEvents(event)
            is DeleteMkopoModalEvent -> handleDeleteLoanModalEvents(event)
            is MkopoDetailsScreenEvent -> handleLoanDetailsScreenEvents(event)
        }
    }

    private fun handleLoanRecordModalEvents(event: MkopoDetailsScreenEvent) {
        when (event) {
            is MkopoRecordModalEvent.OnClickMkopoRecord -> {
                loanRecordModalData.value = LoanRecordModalData(
                    loanRecord = event.displayMkopoRekodi.loanRecord,
                    baseCurrency = event.displayMkopoRekodi.loanRecordCurrencyCode,
                    selectedAccount = event.displayMkopoRekodi.account,
                    createLoanRecordTransaction = event.displayMkopoRekodi.loanRecordTransaction,
                    isLoanInterest = event.displayMkopoRekodi.loanRecord.interest,
                    loanAccountCurrencyCode = event.displayMkopoRekodi.loanCurrencyCode
                )
            }

            is MkopoRecordModalEvent.OnCreateMkopoRecord -> {
                createLoanRecord(event.loanRecordData)
            }

            is MkopoRecordModalEvent.OnDeleteMkopoRecord -> {
                deleteLoanRecord(event.loanRecord)
            }

            MkopoRecordModalEvent.OnDismissMkopoRecord -> {
                loanRecordModalData.value = null
            }

            is MkopoRecordModalEvent.OnEditMkopoRecord -> {
                editLoanRecord(event.loanRecordData)
            }

            else -> {}
        }
    }

    private fun handleLoanModalEvents(event: MkopoDetailsScreenEvent) {
        when (event) {
            MkopoModalEvent.OnDismissMkopoModal -> {
                loanModalData.value = null
            }

            is MkopoModalEvent.OnEditMkopoModal -> {
                editLoan(event.loan, event.createLoanTransaction)
            }

            MkopoModalEvent.PerformCalculation -> {
                waitModalVisible.value = true
            }

            else -> {}
        }
    }

    private fun handleDeleteLoanModalEvents(event: MkopoDetailsScreenEvent) {
        when (event) {
            DeleteMkopoModalEvent.OnDeleteMkopo -> {
                deleteLoan()
                isDeleteModalVisible.value = false
            }

            is DeleteMkopoModalEvent.OnDismissDeleteMkopo -> {
                isDeleteModalVisible.value = event.isDeleteModalVisible
            }

            else -> {}
        }
    }

    private fun handleLoanDetailsScreenEvents(event: MkopoDetailsScreenEvent) {
        when (event) {
            MkopoDetailsScreenEvent.OnAmountClick -> {
                loanModalData.value = LoanModalData(
                    loan = loan.value,
                    baseCurrency = baseCurrency.value,
                    autoFocusKeyboard = false,
                    autoOpenAmountModal = true,
                    selectedAccount = selectedLoanAccount.value,
                    createLoanTransaction = createLoanTransaction.value
                )
            }

            MkopoDetailsScreenEvent.OnEditMkopoClick -> {
                loanModalData.value = LoanModalData(
                    loan = loan.value,
                    baseCurrency = baseCurrency.value,
                    autoFocusKeyboard = false,
                    selectedAccount = selectedLoanAccount.value,
                    createLoanTransaction = createLoanTransaction.value
                )
            }

            MkopoDetailsScreenEvent.OnAddRecord -> {
                loanRecordModalData.value = LoanRecordModalData(
                    loanRecord = null,
                    baseCurrency = baseCurrency.value,
                    selectedAccount = selectedLoanAccount.value
                )
            }

            is MkopoDetailsScreenEvent.OnCreateAccount -> {
                createAccount(event.data)
            }

            else -> {}
        }
    }

    private fun start() {
        load(loanId = screen.loanId)
    }

    private fun load(loanId: UUID) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }.also {
                baseCurrency.value = it
            }

            accounts.value = accountsAct(Unit)

            loan.value = loanByIdAct(loanId)

            loan.value?.let { loan ->
                selectedLoanAccount.value = accounts.value.find {
                    loan.accountId == it.id
                }

                selectedLoanAccount.value?.let { acc ->
                    baseCurrency.value = acc.currency ?: defaultCurrencyCode
                }
            }

            computationThread {
                displayLoanRecords.value =
                    ioThread { loanRecordDao.findAllByLoanId(loanId = loanId) }.map {
                        val trans = ioThread {
                            transactionRepository.findLoanRecordTransaction(
                                it.id
                            )
                        }

                        val account = findAccount(
                            accounts = accounts.value,
                            accountId = it.accountId,
                        )

                        DisplayMkopoRekodi(
                            it.toLegacyDomain(),
                            account = account,
                            loanRecordTransaction = trans != null,
                            loanRecordCurrencyCode = account?.currency ?: defaultCurrencyCode,
                            loanCurrencyCode = selectedLoanAccount.value?.currency
                                ?: defaultCurrencyCode
                        )
                    }.toImmutableList()
            }

            computationThread {
                // Using a local variable to calculate the amount and then reassigning to
                // the State variable to reduce the amount of compose re-draws
                var amtPaid = 0.0
                var loanInterestAmtPaid = 0.0
                displayLoanRecords.value.forEach {
                    // We do not want to calculate records that increase loan.
                    if (it.loanRecord.loanRecordType == LoanRecordType.INCREASE) {
                        return@forEach
                    }
                    val convertedAmount = it.loanRecord.convertedAmount ?: it.loanRecord.amount
                    if (!it.loanRecord.interest) {
                        amtPaid += convertedAmount
                    } else {
                        loanInterestAmtPaid += convertedAmount
                    }
                }

                amountPaid.doubleValue = amtPaid
                loanInterestAmountPaid.doubleValue = loanInterestAmtPaid
            }

            computationThread {
                // Calculate total amount of loan borrowed or lent.
                // That is initial amount + each record that increased the loan.
                val totalAmount =
                    displayLoanRecords.value.fold(loan.value?.amount ?: 0.0) { value, record ->
                        if (record.loanRecord.loanRecordType == LoanRecordType.INCREASE) {
                            val convertedAmount =
                                record.loanRecord.convertedAmount ?: record.loanRecord.amount
                            value + convertedAmount
                        } else {
                            value
                        }
                    }
                loanTotalAmount.doubleValue = totalAmount
            }

            associatedTransaction = ioThread {
                transactionRepository.findLoanTransaction(loanId = loan.value!!.id).let {
                    it?.toLegacy(transactionMapper)
                }
            }

            associatedTransaction?.let {
                createLoanTransaction.value = true
            } ?: run {
                createLoanTransaction.value = false
            }

            TestIdlingResource.decrement()
        }
    }

    fun editLoan(loan: Loan, createLoanTransaction: Boolean = false) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            this@MkopoDetailsVM.loan.value?.let {
                loanTransactionsLogic.Loan.recalculateLoanRecords(
                    oldLoanAccountId = it.accountId,
                    newLoanAccountId = loan.accountId,
                    loanId = loan.id
                )
            }

            loanTransactionsLogic.Loan.editAssociatedLoanTransaction(
                loan = loan,
                createLoanTransaction = createLoanTransaction,
                transaction = associatedTransaction
            )

            loanCreator.edit(loan) {
                load(loanId = it.id)
            }

            TestIdlingResource.decrement()
        }
    }

    private fun deleteLoan() {
        val loan = loan.value ?: return

        viewModelScope.launch {
            TestIdlingResource.increment()

            loanTransactionsLogic.Loan.deleteAssociatedLoanTransactions(loan.id)

            loanCreator.delete(loan) {
                // close screen
                nav.back()
            }

            TestIdlingResource.decrement()
        }
    }

    private fun createLoanRecord(data: CreateLoanRecordData) {
        if (loan.value == null) return
        val loanId = loan.value?.id ?: return
        val localLoan = loan.value!!

        viewModelScope.launch {
            TestIdlingResource.increment()

            val modifiedData = data.copy(
                convertedAmount = loanTransactionsLogic.LoanRecord.calculateConvertedAmount(
                    data = data,
                    loanAccountId = localLoan.accountId
                )
            )

            val loanRecordUUID = loanRecordCreator.create(
                loanId = loanId,
                data = modifiedData
            ) {
                load(loanId = loanId)
            }

            loanRecordUUID?.let {
                loanTransactionsLogic.LoanRecord.createAssociatedLoanRecordTransaction(
                    data = modifiedData,
                    loan = localLoan,
                    loanRecordId = it
                )
            }

            TestIdlingResource.decrement()
        }
    }

    private fun editLoanRecord(editLoanRecordData: EditLoanRecordData) {
        viewModelScope.launch {
            val loanRecord = editLoanRecordData.newLoanRecord
            TestIdlingResource.increment()

            val localLoan: Loan = loan.value ?: return@launch

            val convertedAmount = loanTransactionsLogic.LoanRecord.calculateConvertedAmount(
                loanAccountId = localLoan.accountId,
                newLoanRecord = editLoanRecordData.newLoanRecord,
                oldLoanRecord = editLoanRecordData.originalLoanRecord,
                reCalculateLoanAmount = editLoanRecordData.reCalculateLoanAmount
            )

            val modifiedLoanRecord =
                editLoanRecordData.newLoanRecord.copy(convertedAmount = convertedAmount)

            loanTransactionsLogic.LoanRecord.editAssociatedLoanRecordTransaction(
                loan = localLoan,
                createLoanRecordTransaction = editLoanRecordData.createLoanRecordTransaction,
                loanRecord = loanRecord,
            )

            loanRecordCreator.edit(modifiedLoanRecord) {
                load(loanId = it.loanId)
            }

            TestIdlingResource.decrement()
        }
    }

    private fun deleteLoanRecord(loanRecord: LoanRecord) {
        val loanId = loan.value?.id ?: return

        viewModelScope.launch {
            TestIdlingResource.increment()

            loanRecordCreator.delete(loanRecord) {
                load(loanId = loanId)
            }

            loanTransactionsLogic.LoanRecord.deleteAssociatedLoanRecordTransaction(loanRecordId = loanRecord.id)

            TestIdlingResource.decrement()
        }
    }

    fun onLoanTransactionChecked(boolean: Boolean) {
        createLoanTransaction.value = boolean
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

    private fun findAccount(
        accounts: List<Account>,
        accountId: UUID?,
    ): Account? {
        return accountId?.let { uuid ->
            accounts.find { acc ->
                acc.id == uuid
            }
        }
    }
}
