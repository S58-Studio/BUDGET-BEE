package com.oneSaver.legacy.domain.action.settings

import com.oneSaver.data.database.dao.write.WriteSettingsDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.datamodel.Settings
import javax.inject.Inject

class UpdateSettingsAct @Inject constructor(
    private val writeSettingsDao: WriteSettingsDao
) : FPAction<Settings, Settings>() {
    override suspend fun Settings.compose(): suspend () -> Settings = suspend {
        writeSettingsDao.save(this.toEntity())
        this
    }
}
