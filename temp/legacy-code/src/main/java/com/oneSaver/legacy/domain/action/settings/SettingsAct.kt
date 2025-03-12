package com.oneSaver.allStatus.domain.action.settings

import com.oneSaver.base.legacy.Theme
import com.oneSaver.data.database.dao.read.SettingsDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.Settings
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import javax.inject.Inject

class SettingsAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, Settings>() {
    override suspend fun Unit.compose(): suspend () -> Settings = suspend {
        io { settingsDao.findFirst() }
    } then { it.toLegacyDomain() }

    suspend fun getSettingsWithNextTheme(): Settings {
        val currentSettings = this(Unit)
        val newTheme = when (currentSettings.theme) {
            Theme.LIGHT -> Theme.DARK
            Theme.DARK -> Theme.AUTO
            Theme.AMOLED_DARK -> Theme.AMOLED_DARK
            Theme.AUTO -> Theme.LIGHT
        }
        return currentSettings.copy(theme = newTheme)
    }

    suspend fun getSettings(): Settings = this(Unit)
}