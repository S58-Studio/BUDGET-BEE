package com.financeAndMoney.main

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeAndMoney.accounts.accountTabs.AllAccountTabPage
import com.financeAndMoney.home.HomeTabPage
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.data.model.MainTab
import com.financeAndMoney.legacy.utils.onScreenStart
import com.financeAndMoney.navigation.MainSkreen
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.reportStatements.AllInsightsTabPage
import com.financeAndMoney.controls.ProfileControlsTabPage
import com.financeAndMoney.legacy.mySaveCtx
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateAccountData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.AccountModal
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.AccountModalData

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.MainScreen(screen: MainSkreen, activity: Activity) {
    val viewModel: MainViewModel = viewModel()

    val currency by viewModel.currency.observeAsState("")

    onScreenStart {
        viewModel.start(screen)
        throw RuntimeException("Forced Crashlytics")
    }


    val mysaveContext = mySaveCtx()
    UI(
        screen = screen,
        tab = mysaveContext.mainTab,
        baseCurrency = currency,
        selectTab = viewModel::selectTab,
        onCreateAccount = viewModel::createAccount,
        activity = activity
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: MainSkreen,
    tab: MainTab,

    baseCurrency: String,

    selectTab: (MainTab) -> Unit,
    onCreateAccount: (CreateAccountData) -> Unit,
    activity: Activity
) {
    val nav = navigation()
    when (tab) {
        MainTab.HOME -> HomeTabPage(activity = activity)
        MainTab.INSIGHTS -> AllInsightsTabPage(activity = activity)
        MainTab.ACCOUNTS -> AllAccountTabPage(activity = activity)
        MainTab.PROFILE -> ProfileControlsTabPage()
    }

    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }

    BottomBar(
        tab = tab,
        selectTab = selectTab,

        showAddAccountModal = {
            accountModalData = AccountModalData(
                account = null,
                balance = 0.0,
                baseCurrency = baseCurrency
            )
        }
    )

    AccountModal(
        modal = accountModalData,
        onCreateAccount = onCreateAccount,
        onEditAccount = { _, _ -> },
        dismiss = {
            accountModalData = null
        }
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview
@Composable
private fun PreviewMainScreen() {
    MySavePreview {
        UI(
            screen = MainSkreen,
            tab = MainTab.HOME,
            baseCurrency = "EURO",
            selectTab = {},
            onCreateAccount = { },
            activity = FakeActivity()
        )
    }
}
class FakeActivity : Activity()