package com.oneSaver.allStatus.domain.action.global

import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.monad.Res
import com.oneSaver.frp.monad.thenIfSuccess
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.base.legacy.SharedPrefs
import javax.inject.Inject

class UpdateStartDayOfMonthAct @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val MySaveCtx: MySaveCtx
) : FPAction<Int, Res<String, Int>>() {

    override suspend fun Int.compose(): suspend () -> Res<String, Int> = suspend {
        val startDay = this

        if (startDay in 1..31) {
            Res.Ok(startDay)
        } else {
            Res.Err("Invalid start day $startDay. Start date must be between 1 and 31.")
        }
    } thenIfSuccess { startDay ->
        sharedPrefs.putInt(SharedPrefs.START_DATE_OF_MONTH, startDay)
        MySaveCtx.setStartDayOfMonth(startDay)
        Res.Ok(startDay)
    } thenIfSuccess { startDay ->
        MySaveCtx.initSelectedPeriodInMemory(
            startDayOfMonth = startDay,
            forceReinitialize = true
        )
        Res.Ok(startDay)
    }
}
