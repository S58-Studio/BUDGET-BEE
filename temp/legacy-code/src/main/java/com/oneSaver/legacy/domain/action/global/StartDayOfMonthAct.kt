package com.oneSaver.allStatus.domain.action.global

import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.base.legacy.SharedPrefs
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
