package com.financeAndMoney.legacy.domain.action.settings

import com.financeAndMoney.data.database.dao.write.WriteSettingsDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.legacy.datamodel.Settings
import javax.inject.Inject

class UpdateSettingsAct @Inject constructor(
    private val writeSettingsDao: WriteSettingsDao
) : FPAction<Settings, Settings>() {
    override suspend fun Settings.compose(): suspend () -> Settings = suspend {
        writeSettingsDao.save(this.toEntity())
        this
    }
}
