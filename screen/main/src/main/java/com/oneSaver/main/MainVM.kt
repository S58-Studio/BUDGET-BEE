package com.oneSaver.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.data.repository.CurrencyRepository
import com.oneSaver.domains.usecase.Xchange.SyncXchangeRatesUseCase
import com.oneSaver.frp.test.TestIdlingResource
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.legacy.data.model.MainTab
import com.oneSaver.legacy.domain.deprecated.logic.AccountCreator
import com.oneSaver.legacy.utils.asLiveData
import com.oneSaver.navigation.MainSkreen
import com.oneSaver.navigation.Navigation
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mysaveContext: MySaveCtx,
    private val nav: Navigation,
    private val syncXchangeRatesUseCase: SyncXchangeRatesUseCase,
    private val accountCreator: AccountCreator,
    private val sharedPrefs: SharedPrefs,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    fun start(screen: MainSkreen) {
        nav.onBackPressed[screen] = {
            if (mysaveContext.mainTab == MainTab.ACCOUNTS) {
                mysaveContext.selectMainTab(MainTab.HOME)
                true
            } else {
                false // Exiting the app
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val baseCurrency = currencyRepository.getBaseCurrency()
            withContext(Dispatchers.Main) {
                _currency.value = baseCurrency.code
            }

            sharedPrefs.getBoolean(SharedPrefs.DATA_BACKUP_COMPLETED, false).also {
                mysaveContext.dataBackupCompleted = it
            }

            syncXchangeRatesUseCase.sync(baseCurrency)
        }
    }


    fun selectTab(tab: MainTab) {
        Log.d("MainViewModel", "Selecting tab: $tab")
        mysaveContext.selectMainTab(tab)
    }


    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {}

            TestIdlingResource.decrement()
        }
    }
}
