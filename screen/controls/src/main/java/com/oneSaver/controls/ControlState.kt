package com.oneSaver.controls

import com.oneSaver.base.legacy.Theme

data class ControlState(
    val currencyCode: String,
    val name: String,
    val currentTheme: Theme,
    val lockApp: Boolean,
    val showNotifications: Boolean,
    val hideCurrentBalance: Boolean,
    val hideIncome: Boolean,
    val treatTransfersAsIncomeExpense: Boolean,
    val startDateOfMonth: String,
    val progressState: Boolean,
    val languageOptionVisible: Boolean
)
