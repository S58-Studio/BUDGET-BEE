package com.financeAndMoney.onboarding.steps

import android.app.Activity
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financeAndMoney.base.utils.MySaveAdsManager
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.utils.setStatusBarDarkTextCompat
import com.financeAndMoney.navigation.navigation
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.legacy.domain.data.MysaveCurrency
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.GradientMysave
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.White
import com.financeAndMoney.legacy.legacyOld.ui.theme.components.BackButton
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.CurrencyPicker
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.GradientCutBottom
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.OnboardingButton

@Composable
fun BoxWithConstraintsScope.OnboardingSetCurrency(
    preselectedCurrency: MysaveCurrency,
    onSetCurrency: (MysaveCurrency) -> Unit,
    activity: Activity
) {
    setStatusBarDarkTextCompat(darkText = UI.colors.isLight)
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }
    var currency by remember { mutableStateOf(preselectedCurrency) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Spacer(Modifier.height(16.dp))

        var keyboardVisible by remember {
            mutableStateOf(false)
        }

        val nav = navigation()
        BackButton(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            nav.onBackPressed()
        }

        if (!keyboardVisible) {
            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(R.string.set_currency),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        CurrencyPicker(
            modifier = Modifier
                .fillMaxSize(),
            initialSelectedCurrency = null,
            preselectedCurrency = preselectedCurrency,
            includeKeyboardShownInsetSpacer = true,
            lastItemSpacer = 120.dp,
            onKeyboardShown = { keyboardShown ->
                keyboardVisible = keyboardShown
            }
        ) {
            currency = it
        }
    }

    GradientCutBottom(
        height = 160.dp
    )

    OnboardingButton(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(bottom = 20.dp),

        text = stringResource(R.string.set),
        textColor = White,
        backgroundGradient = GradientMysave,
        hasNext = true,
        enabled = true
    ) {
        if (activity.isFinishing.not() && activity.isDestroyed.not()) {
            val adCallback = MySaveAdsManager.OnAdsCallback {
                onSetCurrency(currency)
            }
            mySaveAdsManager.displayAds(activity, adCallback)
        }
    }
}

//@Preview
//@Composable
//private fun Preview() {
//    MySavePreview {
//        OnboardingSetCurrency(
//            preselectedCurrency = MysaveCurrency.getDefault()
//        ) {
//        }
//    }
//}
