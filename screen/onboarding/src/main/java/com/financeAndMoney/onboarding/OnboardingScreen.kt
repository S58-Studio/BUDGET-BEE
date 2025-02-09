package com.financeAndMoney.onboarding

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.legacy.data.model.AccountBalance
import com.financeAndMoney.legacy.utils.onScreenStart
import com.financeAndMoney.legacy.domain.data.MysaveCurrency
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateAccountData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateCategoryData
import com.financeAndMoney.navigation.OnboardingScreen
import com.financeAndMoney.onboarding.steps.OnboardingAccounts
import com.financeAndMoney.onboarding.steps.OnboardingCategories
import com.financeAndMoney.onboarding.steps.OnboardingSetCurrency
import com.financeAndMoney.onboarding.steps.OnboardingSplashLogin
import com.financeAndMoney.onboarding.steps.OnboardingType
import com.financeAndMoney.onboarding.viewmodel.OnboardingViewModel
import kotlinx.collections.immutable.ImmutableList


@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.OnboardingScreen(screen: OnboardingScreen, activity: Activity) {
    val viewModel: OnboardingViewModel = viewModel()

    val state by viewModel.state
    val uiState = viewModel.uiState()

    val isSystemDarkTheme = isSystemInDarkTheme()
    onScreenStart {
        viewModel.start(
            screen = screen,
            isSystemDarkMode = isSystemDarkTheme
        )
    }

    UI(
        onboardingState = state,
        currency = uiState.currency,

        accountSuggestions = uiState.accountSuggestions,
        accounts = uiState.accounts,

        categorySuggestions = uiState.categorySuggestions,
        categories = uiState.categories,

        onEvent = viewModel::onEvent,
        activity = activity

    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    onboardingState: OnboardingState,
    currency: MysaveCurrency,

    accountSuggestions: ImmutableList<CreateAccountData>,
    accounts: ImmutableList<AccountBalance>,

    categorySuggestions: ImmutableList<CreateCategoryData>,
    categories: ImmutableList<Category>,
    activity: Activity,
    onEvent: (OnboardingEvent) -> Unit = {}

) {
    when (onboardingState) {
        OnboardingState.SPLASH, OnboardingState.LOGIN -> {
            OnboardingSplashLogin(
                onboardingState = onboardingState,
                onSkip = { onEvent(OnboardingEvent.LoginOfflineAccount) },
                activity = activity
            )
        }

        OnboardingState.CHOOSE_PATH -> {
            OnboardingType(
                onStartImport = { onEvent(OnboardingEvent.StartImport) },
                onStartFresh = { onEvent(OnboardingEvent.StartFresh) },
                activity = activity
            )
        }

        OnboardingState.CURRENCY -> {
            OnboardingSetCurrency(
                preselectedCurrency = currency,
                onSetCurrency = { onEvent(OnboardingEvent.SetBaseCurrency(it)) },
                activity = activity
            )
        }

        OnboardingState.ACCOUNTS -> {
            OnboardingAccounts(
                baseCurrency = currency.code,
                suggestions = accountSuggestions,
                accounts = accounts,

                onCreateAccount = { onEvent(OnboardingEvent.CreateAccount(it)) },
                onEditAccount = { account, newBalance ->
                    onEvent(
                        OnboardingEvent.EditAccount(
                            account,
                            newBalance
                        )
                    )
                },

                onDoneClick = { onEvent(OnboardingEvent.OnAddAccountsDone) },
                onSkip = { onEvent(OnboardingEvent.OnAddAccountsSkip) },
                activity = activity
            )
        }

        OnboardingState.CATEGORIES -> {
            OnboardingCategories(
                suggestions = categorySuggestions,
                categories = categories,

                onCreateCategory = { onEvent(OnboardingEvent.CreateCategory(it)) },
                onEditCategory = { onEvent(OnboardingEvent.EditCategory(it)) },

                onDoneClick = { onEvent(OnboardingEvent.OnAddCategoriesDone) },
                onSkip = { onEvent(OnboardingEvent.OnAddCategoriesSkip) },
                activity = activity
            )
        }
    }
}

