package com.oneSaver.allStatus.domain.action.settings

import com.oneSaver.data.database.dao.read.SettingsDao
import com.oneSaver.frp.action.FPAction
import javax.inject.Inject

class BaseCurrencyAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, String>() {
    override suspend fun Unit.compose(): suspend () -> String = suspend {
        io { settingsDao.findFirst().currency }
    }
}
