package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.settings

import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.Settings
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
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