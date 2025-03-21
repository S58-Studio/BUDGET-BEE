package com.oneSaver.allStatus.domain.action.settings

import com.oneSaver.frp.action.FPAction
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
