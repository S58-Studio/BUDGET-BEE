package com.oneSaver.controls

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.base.legacy.Theme
import com.oneSaver.base.legacy.refreshWidget
import com.oneSaver.data.backingUp.BackingUpDataUseCase
import com.oneSaver.data.database.dao.read.SettingsDao
import com.oneSaver.data.database.dao.write.WriteSettingsDao
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.domains.RootScreen
import com.oneSaver.domains.usecase.csv.ExportCsvUseCase
import com.oneSaver.domains.usecase.exchange.SyncXchangeRatesUseCase
import com.oneSaver.frp.monad.Res
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.legacy.LogoutLogic
import com.oneSaver.legacy.domain.action.settings.UpdateSettingsAct
import com.oneSaver.legacy.utils.getISOFormattedDateTime
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.legacy.utils.timeNowUTC
import com.oneSaver.legacy.utils.uiThread
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.allStatus.domain.action.global.StartDayOfMonthAct
import com.oneSaver.allStatus.domain.action.global.UpdateStartDayOfMonthAct
import com.oneSaver.allStatus.domain.action.settings.SettingsAct
import com.oneSaver.widget.mulaBalanc.WalletBalanceWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ControlsVM @Inject constructor(
    private val settingsDao: SettingsDao,
    private val mysaveContext: MySaveCtx,
    private val logoutLogic: LogoutLogic,
    private val sharedPrefs: SharedPrefs,
    private val backingUpDataUseCase: BackingUpDataUseCase,
    private val startDayOfMonthAct: StartDayOfMonthAct,
    private val updateStartDayOfMonthAct: UpdateStartDayOfMonthAct,
    private val syncXchangeRatesUseCase: SyncXchangeRatesUseCase,
    private val settingsAct: SettingsAct,
    private val updateSettingsAct: UpdateSettingsAct,
    private val settingsWriter: WriteSettingsDao,
    private val exportCsvUseCase: ExportCsvUseCase,
    @ApplicationContext private val context: Context
) : ComposeViewModel<ControlState, ControlsEvents>() {

    private val currencyCode = mutableStateOf("")
    private val name = mutableStateOf("")
    private val currentTheme = mutableStateOf<Theme>(Theme.AUTO)
    private val lockApp = mutableStateOf(false)
    private val showNotifications = mutableStateOf(true)
    private val hideCurrentBalance = mutableStateOf(false)
    private val hideIncome = mutableStateOf(false)
    private val treatTransfersAsIncomeExpense = mutableStateOf(false)
    private val startDateOfMonth = mutableIntStateOf(1)
    private val progressState = mutableStateOf(false)

    @Composable
    override fun uiState(): ControlState {
        LaunchedEffect(Unit) {
            onStart()
        }

        return ControlState(
            currencyCode = getCurrencyCode(),
            name = getName(),
            currentTheme = getCurrentTheme(),
            lockApp = getLockApp(),
            showNotifications = getShowNotifications(),
            hideCurrentBalance = getHideCurrentBalance(),
            treatTransfersAsIncomeExpense = getTreatTransfersAsIncomeExpense(),
            startDateOfMonth = getStartDateOfMonth(),
            progressState = getProgressState(),
            hideIncome = getHideIncome(),
            languageOptionVisible = isLanguageOptionVisible()
        )
    }

    private suspend fun onStart() {
        initializeCurrency()
        initializeName()
        initializeCurrentTheme()
        initializeLockApp()
        initializeShowNotifications()
        initializeHideCurrentBalance()
        initializeHideIncome()
        initializeTransfersAsIncomeExpense()
        initializeStartDateOfMonth()
    }

    private suspend fun initializeCurrency() {
        val settings = ioThread {
            settingsDao.findFirst()
        }

        currencyCode.value = settings.currency
    }

    private suspend fun initializeName() {
        val settings = ioThread {
            settingsDao.findFirst()
        }

        name.value = settings.name
    }

    private suspend fun initializeCurrentTheme() {
        currentTheme.value = settingsAct(Unit).theme
    }

    private fun initializeLockApp() {
        lockApp.value = sharedPrefs.getBoolean(SharedPrefs.APP_LOCK_ENABLED, false)
    }

    private fun initializeShowNotifications() {
        showNotifications.value = sharedPrefs.getBoolean(
            SharedPrefs.SHOW_NOTIFICATIONS, true
        )
    }

    private fun initializeHideCurrentBalance() {
        hideCurrentBalance.value =
            sharedPrefs.getBoolean(SharedPrefs.HIDE_CURRENT_BALANCE, false)
    }

    private fun initializeHideIncome() {
        hideIncome.value =
            sharedPrefs.getBoolean(SharedPrefs.HIDE_INCOME, false)
    }

    private fun initializeTransfersAsIncomeExpense() {
        treatTransfersAsIncomeExpense.value =
            sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)
    }

    private suspend fun initializeStartDateOfMonth() {
        startDateOfMonth.intValue = startDayOfMonthAct(Unit)
    }

    @Composable
    private fun getCurrencyCode(): String {
        return currencyCode.value
    }

    @Composable
    private fun getName(): String {
        return name.value
    }

    @Composable
    private fun getCurrentTheme(): Theme {
        return currentTheme.value
    }

    @Composable
    private fun getLockApp(): Boolean {
        return lockApp.value
    }

    @Composable
    private fun getShowNotifications(): Boolean {
        return showNotifications.value
    }

    @Composable
    private fun getHideCurrentBalance(): Boolean {
        return hideCurrentBalance.value
    }

    @Composable
    private fun getHideIncome(): Boolean {
        return hideIncome.value
    }

    @Composable
    private fun getTreatTransfersAsIncomeExpense(): Boolean {
        return treatTransfersAsIncomeExpense.value
    }

    @Composable
    private fun getStartDateOfMonth(): String {
        return startDateOfMonth.intValue.toString()
    }

    @Composable
    private fun getProgressState(): Boolean {
        return progressState.value
    }

    private fun isLanguageOptionVisible(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    override fun onEvent(event: ControlsEvents) {
        when (event) {
            is ControlsEvents.SetCurrency -> setCurrency(event.newCurrency)
            is ControlsEvents.SetName -> setName(event.newName)
            is ControlsEvents.ExportToCsv -> exportToCSV(event.rootScreen)
            is ControlsEvents.BackupData -> exportToZip(event.rootScreen)
            ControlsEvents.SwitchTheme -> switchTheme()
            is ControlsEvents.SetLockApp -> setLockApp(event.lockApp)
            is ControlsEvents.SetShowNotifications -> setShowNotifications(event.showNotifications)
            is ControlsEvents.SetHideCurrentBalance -> setHideCurrentBalance(
                event.hideCurrentBalance
            )

            is ControlsEvents.SetHideIncome -> setHideIncome(
                event.hideIncome
            )

            is ControlsEvents.SetTransfersAsIncomeExpense -> setTransfersAsIncomeExpense(
                event.treatTransfersAsIncomeExpense
            )

            is ControlsEvents.SetStartDateOfMonth -> setStartDateOfMonth(event.startDate)

            ControlsEvents.DeleteCloudUserData -> deleteCloudUserData()
            ControlsEvents.DeleteAllUserData -> deleteAllUserData()
            ControlsEvents.SwitchLanguage -> switchLanguage()
        }
    }

    private fun setCurrency(newCurrency: String) {
        currencyCode.value = newCurrency

        viewModelScope.launch {
            ioThread {
                settingsWriter.save(
                    settingsDao.findFirst().copy(
                        currency = newCurrency
                    )
                )
                AssetCode.from(newCurrency).onRight {
                    syncXchangeRatesUseCase.sync(it)
                }
            }
        }
    }

    private fun setName(newName: String) {
        name.value = newName

        viewModelScope.launch {
            ioThread {
                settingsWriter.save(
                    settingsDao.findFirst().copy(
                        name = newName
                    )
                )
            }
        }
    }

    private fun exportToCSV(rootScreen: RootScreen) {
        mysaveContext.createNewFile(
            "MySaveExport_${
                timeNowUTC().getISOFormattedDateTime()
            }.csv"
        ) { fileUri ->
            viewModelScope.launch {
                exportCsvUseCase.exportToFile(
                    outputFile = fileUri
                )

                rootScreen.shareCSVFile(
                    fileUri = fileUri
                )
            }
        }
    }

    private fun exportToZip(rootScreen: RootScreen) {
        mysaveContext.createNewFile(
            "MySaveBackup_${
                timeNowUTC().getISOFormattedDateTime()
            }.zip"
        ) { fileUri ->
            viewModelScope.launch(Dispatchers.IO) {
                progressState.value = true
                backingUpDataUseCase.exportToFile(zipFileUri = fileUri)
                progressState.value = false

                sharedPrefs.putBoolean(SharedPrefs.DATA_BACKUP_COMPLETED, true)
                mysaveContext.dataBackupCompleted = true

                uiThread {
                    rootScreen.shareZipFile(
                        fileUri = fileUri
                    )
                }
            }
        }
    }

    private fun switchTheme() {
        viewModelScope.launch {
            settingsAct.getSettingsWithNextTheme().run {
                updateSettingsAct(this)
                mysaveContext.switchTheme(this.theme)
                currentTheme.value = this.theme
            }
        }
    }

    private fun setLockApp(lock: Boolean) {
        lockApp.value = lock

        viewModelScope.launch {
            sharedPrefs.putBoolean(SharedPrefs.APP_LOCK_ENABLED, lock)
            refreshWidget(WalletBalanceWidgetReceiver::class.java)
        }
    }

    private fun setShowNotifications(notificationsShow: Boolean) {
        showNotifications.value = notificationsShow

        viewModelScope.launch {
            sharedPrefs.putBoolean(SharedPrefs.SHOW_NOTIFICATIONS, notificationsShow)
        }
    }

    private fun setHideCurrentBalance(hideBalance: Boolean) {
        hideCurrentBalance.value = hideBalance

        viewModelScope.launch {
            sharedPrefs.putBoolean(SharedPrefs.HIDE_CURRENT_BALANCE, hideBalance)
        }
    }

    private fun setHideIncome(isHideIncome: Boolean) {
        hideIncome.value = isHideIncome

        viewModelScope.launch {
            sharedPrefs.putBoolean(SharedPrefs.HIDE_INCOME, isHideIncome)
        }
    }

    private fun setTransfersAsIncomeExpense(setTransfersAsIncomeExpense: Boolean) {
        treatTransfersAsIncomeExpense.value = setTransfersAsIncomeExpense

        viewModelScope.launch {
            sharedPrefs.putBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                treatTransfersAsIncomeExpense.value
            )
        }
    }

    private fun setStartDateOfMonth(startDate: Int) {
        viewModelScope.launch {
            when (val res = updateStartDayOfMonthAct(startDate)) {
                is Res.Err -> {}
                is Res.Ok -> {
                    startDateOfMonth.intValue = res.data
                }
            }
        }
    }

    private fun deleteCloudUserData() {
        viewModelScope.launch {
            cloudLogout()
        }
    }

    private fun cloudLogout() {
        viewModelScope.launch {
            logoutLogic.cloudLogout()
        }
    }

    private fun deleteAllUserData() {
        viewModelScope.launch {
            logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutLogic.logout()
        }
    }

    private fun switchLanguage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.fromParts("package", context.packageName, null)
            context.applicationContext.startActivity(intent)
        }
    }
}
