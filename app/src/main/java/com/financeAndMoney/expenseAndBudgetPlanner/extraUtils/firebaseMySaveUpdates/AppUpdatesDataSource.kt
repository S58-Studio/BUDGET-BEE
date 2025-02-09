package com.financeAndMoney.expenseAndBudgetPlanner.extraUtils.firebaseMySaveUpdates

import android.content.Context

class AppUpdatesDataSource(context: Context) {

    private val appUpdatesDao: AppUpdatesDao

    init {
        val dbHelper = DBHelper.getInstance(context)
        appUpdatesDao = dbHelper.getAppUpdatesDao()
    }

    /* App Updates */
    fun addAppUpdates(updates: AppUpdates) {
        appUpdatesDao.addAppUpdates(updates)
    }

    fun getAppUpdates(): AppUpdates? {
        return appUpdatesDao.getAppUpdates()
    }

    fun clearAppUpdates() {
        appUpdatesDao.clearUpdates()
    }
}