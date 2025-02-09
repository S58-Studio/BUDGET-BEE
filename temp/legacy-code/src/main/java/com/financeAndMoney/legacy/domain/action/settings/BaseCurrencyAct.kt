package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.settings

import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.frp.action.FPAction
import javax.inject.Inject

class BaseCurrencyAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, String>() {
    override suspend fun Unit.compose(): suspend () -> String = suspend {
        io { settingsDao.findFirst().currency }
    }
}
