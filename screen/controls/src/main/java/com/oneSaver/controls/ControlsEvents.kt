package com.oneSaver.controls

import com.oneSaver.domains.RootScreen

sealed interface ControlsEvents {
    data class SetCurrency(val newCurrency: String) : ControlsEvents
    data class SetName(val newName: String) : ControlsEvents
    data class ExportToCsv(val rootScreen: RootScreen) : ControlsEvents
    data class BackupData(val rootScreen: RootScreen) : ControlsEvents
    data object SwitchTheme : ControlsEvents
    data class SetLockApp(val lockApp: Boolean) : ControlsEvents
    data class SetShowNotifications(val showNotifications: Boolean) : ControlsEvents
    data class SetHideCurrentBalance(val hideCurrentBalance: Boolean) : ControlsEvents
    data class SetHideIncome(val hideIncome: Boolean) : ControlsEvents
    data class SetTransfersAsIncomeExpense(val treatTransfersAsIncomeExpense: Boolean) :
        ControlsEvents

    data class SetStartDateOfMonth(val startDate: Int) : ControlsEvents
    data object DeleteCloudUserData : ControlsEvents
    data object DeleteAllUserData : ControlsEvents
    data object SwitchLanguage : ControlsEvents
}
