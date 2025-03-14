package com.oneSaver.onboarding.steps

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oneSaver.base.utils.MySaveAdsManager
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.navigation.navigation
import com.oneSaver.onboarding.components.OnboardingProgressSlider
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.GradientMysave
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.Orange
import com.oneSaver.allStatus.userInterface.theme.White
import com.oneSaver.legacy.legacyOld.ui.theme.components.CloseButton
import com.oneSaver.allStatus.userInterface.theme.components.IvyOutlinedButtonFillMaxWidth
import com.oneSaver.allStatus.userInterface.theme.components.OnboardingButton

@Composable
fun OnboardingType(
    onStartImport: () -> Unit,
    onStartFresh: () -> Unit,
    activity: Activity
) {
    val mySaveAdsManager = remember { MySaveAdsManager.getInstance() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Spacer(Modifier.height(16.dp))

            val nav = navigation()
            CloseButton(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                nav.onBackPressed()
            }

            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(R.string.import_csv_file),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(R.string.from_my_save_or_another_app),
                style = UI.typo.nB2.style(
                    fontWeight = FontWeight.Bold,
                    color = Gray
                )
            )

            Spacer(Modifier.weight(1f))

            Image(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.onboarding_illustration_import),
                contentDescription = "import illustration"
            )

            OnboardingProgressSlider(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                selectedStep = 0,
                stepsCount = 4,
                selectedColor = Orange
            )

            Spacer(Modifier.weight(1f))

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(R.string.importing_another_time_warning),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(16.dp))

            IvyOutlinedButtonFillMaxWidth(
                modifier = Modifier
                    .padding(16.dp),
                text = stringResource(R.string.import_backup_file),
                iconStart = R.drawable.ic_export_csv,
                iconTint = Green,
                textColor = Green
            ) {
                onStartImport()
            }

            Spacer(Modifier.weight(1f))

            OnboardingButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.start_fresh),
                textColor = White,
                backgroundGradient = GradientMysave,
                hasNext = true,
                enabled = true
            ) {
                if (activity.isFinishing.not() && activity.isDestroyed.not()) {
                    val adCallback = MySaveAdsManager.OnAdsCallback {
                        onStartFresh()
                    }
                    mySaveAdsManager.displayAds(activity, adCallback)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}


//@Preview
//@Composable
//private fun Preview() {
//    MySavePreview {
//        OnboardingType(
//            onStartImport = {}
//        ) {
//        }
//    }
//}

