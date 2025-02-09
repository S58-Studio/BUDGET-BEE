package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.settings

import com.financeAndMoney.frp.action.FPAction
import java.math.BigDecimal
import javax.inject.Inject

class CalcBufferDiffAct @Inject constructor() : FPAction<CalcBufferDiffAct.Input, BigDecimal>() {

    override suspend fun Input.compose(): suspend () -> BigDecimal = {
        balance - buffer
    }

    data class Input(
        val balance: BigDecimal,
        val buffer: BigDecimal
    )
}
