package com.oneSaver.onboarding.viewmodel

import androidx.compose.runtime.MutableState
import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.domains.usecase.exchange.SyncXchangeRatesUseCase
import com.oneSaver.legacy.LogoutLogic
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.legacy.data.model.AccountBalance
import com.oneSaver.legacy.utils.OpResult
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.navigation.ImportingSkrin
import com.oneSaver.navigation.MainSkreen
import com.oneSaver.navigation.Navigation
import com.oneSaver.navigation.OnboardingScreen
import com.oneSaver.onboarding.OnboardingState
import com.oneSaver.legacy.domain.data.MysaveCurrency
import com.oneSaver.allStatus.domain.deprecated.logic.PreloadDataLogic
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import com.oneSaver.legacy.notification.TransactionReminderLogic
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingRouter(
    private val opGoogleSignIn: MutableState<OpResult<Unit>?>,
    private val state: MutableState<OnboardingState>,
    private val accounts: MutableState<ImmutableList<AccountBalance>>,
    private val accountSuggestions: MutableState<ImmutableList<CreateAccountData>>,
    private val categories: MutableState<ImmutableList<Category>>,
    private val categorySuggestions: MutableState<ImmutableList<CreateCategoryData>>,

    private val nav: Navigation,
    private val accountDao: AccountDao,
    private val sharedPrefs: SharedPrefs,
    private val transactionReminderLogic: TransactionReminderLogic,
    private val preloadDataLogic: PreloadDataLogic,
    private val categoryRepository: CategoryRepository,
    private val logoutLogic: LogoutLogic,
    private val syncXchangeRatesUseCase: SyncXchangeRatesUseCase,
) {

    var isLoginCache = false

    fun initBackHandling(
        screen: OnboardingScreen,
        viewModelScope: CoroutineScope,
        restartOnboarding: () -> Unit
    ) {
        nav.onBackPressed[screen] = {
            when (state.value) {
                OnboardingState.SPLASH -> {
                    // do nothing, consume back
                    true
                }

                OnboardingState.LOGIN -> {
                    // let the user exit the app
                    false
                }

                OnboardingState.CHOOSE_PATH -> {
                    state.value = OnboardingState.LOGIN
                    true
                }

                OnboardingState.CURRENCY -> {
                    if (isLoginCache) {
                        // user with Ivy account
                        viewModelScope.launch {
                            logoutLogic.logout()
                            isLoginCache = false
                            restartOnboarding()
                            state.value = OnboardingState.LOGIN
                        }
                    } else {
                        // fresh user
                        state.value = OnboardingState.CHOOSE_PATH
                    }
                    true
                }

                OnboardingState.ACCOUNTS -> {
                    state.value = OnboardingState.CURRENCY
                    true
                }

                OnboardingState.CATEGORIES -> {
                    state.value = OnboardingState.ACCOUNTS
                    true
                }

                null -> {
                    // do nothing, consume back
                    true
                }
            }
        }
    }

    // ------------------------------------- Step 0 - Splash ----------------------------------------
    suspend fun splashNext() {
        if (state.value == OnboardingState.SPLASH) {
            delay(1000)

            state.value = OnboardingState.LOGIN
        }
    }
    // ------------------------------------- Step 0 -------------------------------------------------

    // ------------------------------------- Step 1 - Login -----------------------------------------
    suspend fun googleLoginNext() {
        if (isLogin()) {
            // Route logged user
            state.value = OnboardingState.CURRENCY
        } else {
            // Route new user
            state.value = OnboardingState.CHOOSE_PATH
        }
    }

    private suspend fun isLogin(): Boolean {
        isLoginCache = ioThread { accountDao.findAll().isNotEmpty() }
        return isLoginCache
    }

    suspend fun offlineAccountNext() {
        state.value = OnboardingState.CHOOSE_PATH
    }
    // ------------------------------------- Step 1 -------------------------------------------------

    // ------------------------------------- Step 2 - Choose path -----------------------------------
    fun startImport() {
        nav.navigateTo(
            ImportingSkrin(
                launchedFromOnboarding = true
            )
        )
    }

    fun importSkip() {
        state.value = OnboardingState.CURRENCY
    }

    fun importFinished(success: Boolean) {
        if (success) {
            state.value = OnboardingState.CURRENCY
        }
    }

    fun startFresh() {
        state.value = OnboardingState.CURRENCY
    }
    // ------------------------------------- Step 2 -------------------------------------------------

    // ------------------------------------- Step 3 - Currency --------------------------------------
    suspend fun setBaseCurrencyNext(
        baseCurrency: MysaveCurrency,
        accountsWithBalance: suspend () -> ImmutableList<AccountBalance>,
    ) {
        routeToAccounts(
            baseCurrency = baseCurrency,
            accountsWithBalance = accountsWithBalance
        )

        if (isLogin()) {
            completeOnboarding(baseCurrency = baseCurrency)
        }
    }
    // ------------------------------------- Step 3 -------------------------------------------------

    // ------------------------------------- Step 4 - Accounts --------------------------------------
    suspend fun accountsNext() {
        routeToCategories()
    }

    suspend fun accountsSkip() {
        routeToCategories()

        ioThread {
            preloadDataLogic.preloadAccounts()
        }
    }
    // ------------------------------------- Step 4 -------------------------------------------------

    // ------------------------------------- Step 5 - Categories ------------------------------------
    suspend fun categoriesNext(baseCurrency: MysaveCurrency?) {
        completeOnboarding(baseCurrency = baseCurrency)
    }

    suspend fun categoriesSkip(baseCurrency: MysaveCurrency?) {
        completeOnboarding(baseCurrency = baseCurrency)

        ioThread {
            preloadDataLogic.preloadCategories()
        }
    }
    // ------------------------------------- Step 5 -------------------------------------------------

    // -------------------------------------- Routes ------------------------------------------------
    private suspend fun routeToAccounts(
        baseCurrency: MysaveCurrency,
        accountsWithBalance: suspend () -> ImmutableList<AccountBalance>,
    ) {
        val accounts = accountsWithBalance()
        this.accounts.value = accounts

        accountSuggestions.value =
            preloadDataLogic.accountSuggestions(baseCurrency.code)
        state.value = OnboardingState.ACCOUNTS
    }

    private suspend fun routeToCategories() {
        categories.value =
            ioThread { categoryRepository.findAll().toImmutableList() }
        categorySuggestions.value = preloadDataLogic.categorySuggestions()

        state.value = OnboardingState.CATEGORIES
    }

    private suspend fun completeOnboarding(
        baseCurrency: MysaveCurrency?
    ) {
        sharedPrefs.putBoolean(SharedPrefs.ONBOARDING_COMPLETED, true)

        navigateOutOfOnboarding()

        // the rest below is not UI stuff so I don't care
        ioThread {
            transactionReminderLogic.scheduleReminder()

            AssetCode.from(baseCurrency?.code ?: MysaveCurrency.getDefault().code)
                .onRight {
                    syncXchangeRatesUseCase.sync(baseCurrency = it)
                }
        }

        resetState()
    }

    private fun resetState() {
        state.value = OnboardingState.SPLASH
        opGoogleSignIn.value = null
    }

    private fun navigateOutOfOnboarding() {
        nav.resetBackStack()
        nav.navigateTo(MainSkreen)
    }
    // -------------------------------------- Routes ------------------------------------------------
}
