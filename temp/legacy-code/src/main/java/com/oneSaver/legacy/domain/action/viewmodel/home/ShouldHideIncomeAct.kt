package com.oneSaver.legacy.domain.action.viewmodel.home

import com.oneSaver.frp.action.FPAction
import com.oneSaver.base.legacy.SharedPrefs
import javax.inject.Inject

class ShouldHideIncomeAct @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : FPAction<Unit, Boolean>() {
    override suspend fun Unit.compose(): suspend () -> Boolean = {
        sharedPrefs.getBoolean(
            SharedPrefs.HIDE_INCOME,
            false
        )
    }
}
