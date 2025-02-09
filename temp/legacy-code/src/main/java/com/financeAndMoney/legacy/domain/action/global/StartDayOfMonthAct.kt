package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.global

import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.MySaveCtx
import com.financeAndMoney.base.legacy.SharedPrefs
import javax.inject.Inject

class StartDayOfMonthAct @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val MySaveCtx: MySaveCtx
) : FPAction<Unit, Int>() {

    override suspend fun Unit.compose(): suspend () -> Int = suspend {
        sharedPrefs.getInt(SharedPrefs.START_DATE_OF_MONTH, 1)
    } then { startDay ->
        MySaveCtx.setStartDayOfMonth(startDay)
        startDay
    }
}
