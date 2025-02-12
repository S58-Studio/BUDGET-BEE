package com.oneSaver

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import com.oneSaver.accounts.AccountsTab
import com.oneSaver.attributions.AttributionsScreenImpl
import com.oneSaver.budgets.BudgetScreen
import com.oneSaver.categories.CategoriesScreen
import com.oneSaver.controls.ProfileControlsTabPage
import com.oneSaver.disclaimer.DisclaimerScreenImpl
import com.oneSaver.exchangerates.ExchangeRatesScreen
import com.oneSaver.features.FeaturesScreenImpl
import com.oneSaver.iliyopangwa.editUpcomingPayments.ModifyPlanedSkrin
import com.oneSaver.iliyopangwa.list.ScheduledPaymntsSkrin
import com.oneSaver.importdata.csv.CSVScreen
import com.oneSaver.importdata.csvimport.ImportCSVScreen
import com.oneSaver.loans.mkopo.MkopoSkrini
import com.oneSaver.loans.mkopoDetails.LoanDetailsScreen
import com.oneSaver.main.MainScreen
import com.oneSaver.mulaBalanc.BalanceScreen
import com.oneSaver.navigation.AkauntiTabSkrin
import com.oneSaver.navigation.AttributionsScreen
import com.oneSaver.navigation.BudgetScreen
import com.oneSaver.navigation.BalanceSkrin
import com.oneSaver.navigation.CSVScreen
import com.oneSaver.navigation.DisclaimerScreen
import com.oneSaver.navigation.FeatureSkrin
import com.oneSaver.navigation.FinPieChartStatisticSkrin
import com.oneSaver.navigation.ImportingSkrin
import com.oneSaver.navigation.KategoriSkrin
import com.oneSaver.navigation.MainSkreen
import com.oneSaver.navigation.MkopoDetailsSkrin
import com.oneSaver.navigation.LoanScreen
import com.oneSaver.navigation.ModifyScheduledSkrin
import com.oneSaver.navigation.ModifyTransactionSkrin
import com.oneSaver.navigation.OnboardingScreen
import com.oneSaver.navigation.ScheduledPaymntsSkrin
import com.oneSaver.navigation.Screen
import com.oneSaver.navigation.SeekSkrin
import com.oneSaver.navigation.SettingSkrin
import com.oneSaver.navigation.TransactScrin
import com.oneSaver.navigation.XchangeRatesSkrin
import com.oneSaver.onboarding.OnboardingScreen
import com.oneSaver.piechart.FinPieChartStatisticSkrin
import com.oneSaver.seek.SeekSkrin
import com.oneSaver.transaction.EditTransactionScreen
import com.oneSaver.transfers.TransferSkrin

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
