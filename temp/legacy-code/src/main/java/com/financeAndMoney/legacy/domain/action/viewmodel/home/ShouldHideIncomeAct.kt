package com.financeAndMoney.legacy.domain.action.viewmodel.home

import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.base.legacy.SharedPrefs
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
