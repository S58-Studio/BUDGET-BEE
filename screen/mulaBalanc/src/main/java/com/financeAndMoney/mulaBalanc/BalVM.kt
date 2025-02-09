package com.financeAndMoney.mulaBalanc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.financeAndMoney.userInterface.ComposeViewModel
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.legacy.utils.ioThread
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.settings.BaseCurrencyAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.wallet.CalcWalletBalanceAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.PlannedPaymentsLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class BalVM @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val ivyContext: com.financeAndMoney.legacy.MySaveCtx,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct
) : ComposeViewModel<BalState, BalEvent>() {

    private val period = mutableStateOf(ivyContext.selectedPeriod)
    private val baseCurrencyCode = mutableStateOf("")
    private val currentBalance = mutableDoubleStateOf(0.0)
    private val plannedPaymentsAmount = mutableDoubleStateOf(0.0)
    private val balanceAfterPlannedPayments = mutableDoubleStateOf(0.0)
    private val numberOfMonthsAhead = mutableIntStateOf(1)

    @Composable
    override fun uiState(): BalState {
        LaunchedEffect(Unit) {
            start()
        }

        return BalState(
            period = period.value,
            balanceAfterPlannedPayments = balanceAfterPlannedPayments.doubleValue,
            currentBalance = currentBalance.doubleValue,
            baseCurrencyCode = baseCurrencyCode.value,
            plannedPaymentsAmount = plannedPaymentsAmount.doubleValue
        )
    }

    override fun onEvent(event: BalEvent) {
        when (event) {
            is BalEvent.OnNextMonth -> nextMonth()
            is BalEvent.OnSetPeriod -> setPeriod(event.timePeriod)
            is BalEvent.OnPreviousMonth -> previousMonth()
        }
    }

    private fun start(
        timePeriod: TimePeriod = ivyContext.selectedPeriod
    ) {
        viewModelScope.launch {
            baseCurrencyCode.value = baseCurrencyAct(Unit)
            period.value = timePeriod

            currentBalance.doubleValue = calcWalletBalanceAct(
                CalcWalletBalanceAct.Input(baseCurrencyCode.value)
            ).toDouble()

            plannedPaymentsAmount.doubleValue = ioThread {
                plannedPaymentsLogic.plannedPaymentsAmountFor(
                    timePeriod.toRange(ivyContext.startDayOfMonth)
                    // + positive if Income > Expenses else - negative
                ) * if (numberOfMonthsAhead.intValue >= 0) {
                    numberOfMonthsAhead.intValue.toDouble()
                } else {
                    1.0
                }
            }
            balanceAfterPlannedPayments.doubleValue =
                currentBalance.doubleValue + plannedPaymentsAmount.doubleValue
        }
    }

    private fun setPeriod(timePeriod: TimePeriod) {
        start(timePeriod = timePeriod)
    }

    private fun nextMonth() {
        val month = period.value.month
        val year = period.value.year ?: com.financeAndMoney.legacy.utils.dateNowUTC().year
        numberOfMonthsAhead.intValue += 1
        if (month != null) {
            start(
                timePeriod = month.incrementMonthPeriod(ivyContext, 1L, year = year)
            )
        }
    }

    private fun previousMonth() {
        val month = period.value.month
        val year = period.value.year ?: com.financeAndMoney.legacy.utils.dateNowUTC().year
        numberOfMonthsAhead.intValue -= 1
        if (month != null) {
            start(
                timePeriod = month.incrementMonthPeriod(ivyContext, -1L, year = year)
            )
        }
    }
}
