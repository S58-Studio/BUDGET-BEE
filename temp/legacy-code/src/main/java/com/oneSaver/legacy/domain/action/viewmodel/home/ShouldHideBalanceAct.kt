package com.oneSaver.allStatus.domain.action.viewmodel.home

import com.oneSaver.frp.action.FPAction
import com.oneSaver.base.legacy.SharedPrefs
import javax.inject.Inject

class ShouldHideBalanceAct @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : FPAction<Unit, Boolean>() {
    override suspend fun Unit.compose(): suspend () -> Boolean = {
        sharedPrefs.getBoolean(
            SharedPrefs.HIDE_CURRENT_BALANCE,
            false
        )
    }
}
