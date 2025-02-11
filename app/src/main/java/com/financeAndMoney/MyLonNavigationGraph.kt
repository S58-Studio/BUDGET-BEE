package com.financeAndMoney

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import com.financeAndMoney.accounts.AccountsTab
import com.financeAndMoney.attributions.AttributionsScreenImpl
import com.financeAndMoney.budgets.BudgetScreen
import com.financeAndMoney.categories.CategoriesScreen
import com.financeAndMoney.controls.ProfileControlsTabPage
import com.financeAndMoney.disclaimer.DisclaimerScreenImpl
import com.financeAndMoney.exchangerates.ExchangeRatesScreen
import com.financeAndMoney.features.FeaturesScreenImpl
import com.financeAndMoney.iliyopangwa.editUpcomingPayments.ModifyPlanedSkrin
import com.financeAndMoney.iliyopangwa.list.ScheduledPaymntsSkrin
import com.financeAndMoney.importdata.csv.CSVScreen
import com.financeAndMoney.importdata.csvimport.ImportCSVScreen
import com.financeAndMoney.loans.mkopo.MkopoSkrini
import com.financeAndMoney.loans.mkopoDetails.LoanDetailsScreen
import com.financeAndMoney.main.MainScreen
import com.financeAndMoney.mulaBalanc.BalanceScreen
import com.financeAndMoney.navigation.AkauntiTabSkrin
import com.financeAndMoney.navigation.AttributionsScreen
import com.financeAndMoney.navigation.BudgetScreen
import com.financeAndMoney.navigation.BalanceSkrin
import com.financeAndMoney.navigation.CSVScreen
import com.financeAndMoney.navigation.DisclaimerScreen
import com.financeAndMoney.navigation.FeatureSkrin
import com.financeAndMoney.navigation.FinPieChartStatisticSkrin
import com.financeAndMoney.navigation.ImportingSkrin
import com.financeAndMoney.navigation.KategoriSkrin
import com.financeAndMoney.navigation.MainSkreen
import com.financeAndMoney.navigation.MkopoDetailsSkrin
import com.financeAndMoney.navigation.LoanScreen
import com.financeAndMoney.navigation.ModifyScheduledSkrin
import com.financeAndMoney.navigation.ModifyTransactionSkrin
import com.financeAndMoney.navigation.OnboardingScreen
import com.financeAndMoney.navigation.ScheduledPaymntsSkrin
import com.financeAndMoney.navigation.Screen
import com.financeAndMoney.navigation.SeekSkrin
import com.financeAndMoney.navigation.SettingSkrin
import com.financeAndMoney.navigation.TransactScrin
import com.financeAndMoney.navigation.XchangeRatesSkrin
import com.financeAndMoney.onboarding.OnboardingScreen
import com.financeAndMoney.piechart.FinPieChartStatisticSkrin
import com.financeAndMoney.seek.SeekSkrin
import com.financeAndMoney.transaction.EditTransactionScreen
import com.financeAndMoney.transfers.TransferSkrin

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
@Suppress("CyclomaticComplexMethod", "FunctionNaming")
fun BoxWithConstraintsScope.MysaveNavGraph(screen: Screen?, activity: Activity) {
    when (screen) {
        null -> {
            // show nothing
        }

        is MainSkreen -> MainScreen(screen = screen, activity = activity)
        is OnboardingScreen ->  OnboardingScreen(screen = screen, activity = activity)
        is XchangeRatesSkrin -> ExchangeRatesScreen()
        is ModifyTransactionSkrin -> EditTransactionScreen(screen = screen)
        is TransactScrin -> TransferSkrin(screen = screen)
        is FinPieChartStatisticSkrin -> FinPieChartStatisticSkrin(screen = screen, activity = activity)
        is KategoriSkrin -> CategoriesScreen(screen = screen, activity = activity)
        is AkauntiTabSkrin ->AccountsTab(screen = screen)
        is SettingSkrin -> ProfileControlsTabPage()
        is ScheduledPaymntsSkrin -> ScheduledPaymntsSkrin(screen = screen, activity = activity)
        is ModifyScheduledSkrin -> ModifyPlanedSkrin(screen = screen)
        is BalanceSkrin -> BalanceScreen(screen = screen, activity = activity)
        is ImportingSkrin -> ImportCSVScreen(screen = screen)
        //is ReportScreen -> ReportScreen(screen = screen)
        is BudgetScreen -> BudgetScreen(screen = screen, activity = activity)
        is LoanScreen -> MkopoSkrini(screen = screen, activity = activity)
        is MkopoDetailsSkrin -> LoanDetailsScreen(screen = screen, activity = activity)
        is SeekSkrin -> SeekSkrin(screen = screen)
        is CSVScreen -> CSVScreen(screen = screen)
        FeatureSkrin -> FeaturesScreenImpl()
        AttributionsScreen -> AttributionsScreenImpl()
        DisclaimerScreen -> DisclaimerScreenImpl()
    }
}
