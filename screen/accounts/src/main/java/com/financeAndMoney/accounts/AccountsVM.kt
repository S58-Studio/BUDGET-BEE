package com.financeAndMoney.accounts

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.financeAndMoney.base.legacy.SharedPrefs
import com.financeAndMoney.data.DataObserver
import com.financeAndMoney.data.DataWriteEvent
import com.financeAndMoney.data.repository.AccountRepository
import com.financeAndMoney.legacy.MySaveCtx
import com.financeAndMoney.legacy.data.model.AccountData
import com.financeAndMoney.legacy.data.model.toCloseTimeRange
import com.financeAndMoney.legacy.utils.format
import com.financeAndMoney.legacy.utils.ioThread
import com.financeAndMoney.userInterface.ComposeViewModel
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.settings.BaseCurrencyAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.account.AccountDataAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.wallet.CalcWalletBalanceAct
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AccountsVM @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val mySaveContext: MySaveCtx,
    private val sharedPrefs: SharedPrefs,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val accountDataAct: AccountDataAct,
    private val accountRepository: AccountRepository,
    private val dataObserver: DataObserver,
) : ComposeViewModel<ACState, ACEventss>() {
    private val baseCurrency = mutableStateOf("")
    private val accountsData = mutableStateOf(listOf<AccountData>())
    private val totalBalanceWithExcluded = mutableStateOf("")
    private val totalBalanceWithExcludedText = mutableStateOf("")
    private val totalBalanceWithoutExcluded = mutableStateOf("")
    private val totalBalanceWithoutExcludedText = mutableStateOf("")
    private val reorderVisible = mutableStateOf(false)

    init {
        viewModelScope.launch {
            dataObserver.writeEvents.collectLatest { event ->
                when (event) {
                    is DataWriteEvent.AccountChange -> {
                        onStart()
                    }

                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    @Composable
    override fun uiState(): ACState {
        LaunchedEffect(Unit) {
            onStart()
        }

        return ACState(
            baseCurrency = getBaseCurrency(),
            accountsData = getAccountsData(),
            totalBalanceWithExcluded = getTotalBalanceWithExcluded(),
            totalBalanceWithExcludedText = getTotalBalanceWithExcludedText(),
            totalBalanceWithoutExcluded = getTotalBalanceWithoutExcluded(),
            totalBalanceWithoutExcludedText = getTotalBalanceWithoutExcludedText(),
            reorderVisible = getReorderVisible()
        )
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency.value
    }

    @Composable
    private fun getAccountsData(): ImmutableList<AccountData> {
        return accountsData.value.toImmutableList()
    }

    @Composable
    private fun getTotalBalanceWithExcluded(): String {
        return totalBalanceWithExcluded.value
    }

    @Composable
    private fun getTotalBalanceWithExcludedText(): String {
        return totalBalanceWithExcludedText.value
    }

    @Composable
    private fun getTotalBalanceWithoutExcluded(): String {
        return totalBalanceWithoutExcluded.value
    }

    @Composable
    private fun getTotalBalanceWithoutExcludedText(): String {
        return totalBalanceWithoutExcludedText.value
    }

    @Composable
    private fun getReorderVisible(): Boolean {
        return reorderVisible.value
    }

    override fun onEvent(event: ACEventss) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is ACEventss.OnReorder -> reorder(event.reorderedList)
                is ACEventss.OnReorderModalVisible -> reorderModalVisible(event.reorderVisible)
            }
        }
    }

    private suspend fun reorder(newOrder: List<AccountData>) {
        ioThread {
            newOrder.mapIndexed { index, accountData ->
                accountRepository.save(accountData.account.copy(orderNum = index.toDouble()))
            }
        }

        startInternally()
    }

    private fun onStart() {
        viewModelScope.launch(Dispatchers.Default) {
            startInternally()
        }
    }

    private suspend fun startInternally() {
        val period = com.financeAndMoney.legacy.data.model.TimePeriod.currentMonth(
            startDayOfMonth = mySaveContext.startDayOfMonth
        ) // this must be monthly
        val range = period.toRange(mySaveContext.startDayOfMonth)

        val baseCurrencyCode = baseCurrencyAct(Unit)
        val accounts = accountRepository.findAll().toImmutableList()

        val includeTransfersInCalc =
            sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)

        val accountsDataList = accountDataAct(
            AccountDataAct.Input(
                accounts = accounts,
                range = range.toCloseTimeRange(),
                baseCurrency = baseCurrencyCode,
                includeTransfersInCalc = includeTransfersInCalc
            )
        )

        val totalBalanceWithExcludedAccounts = calcWalletBalanceAct(
            CalcWalletBalanceAct.Input(
                baseCurrency = baseCurrencyCode,
                withExcluded = true
            )
        ).toDouble()

        val totalBalanceWithoutExcludedAccounts = calcWalletBalanceAct(
            CalcWalletBalanceAct.Input(
                baseCurrency = baseCurrencyCode
            )
        ).toDouble()

        baseCurrency.value = baseCurrencyCode
        accountsData.value = accountsDataList
        totalBalanceWithExcluded.value = totalBalanceWithExcludedAccounts.toString()
        totalBalanceWithExcludedText.value = context.getString(
            R.string.total,
            baseCurrencyCode,
            totalBalanceWithExcludedAccounts.format(
                baseCurrencyCode
            )
        )
        totalBalanceWithoutExcluded.value = totalBalanceWithoutExcludedAccounts.toString()
        totalBalanceWithoutExcludedText.value = context.getString(
            R.string.total_exclusive,
            baseCurrencyCode,
            totalBalanceWithoutExcludedAccounts.format(
                baseCurrencyCode
            )
        )
    }

    private fun reorderModalVisible(visible: Boolean) {
        reorderVisible.value = visible
    }
}
