package com.oneSaver.importdata.csvimport.flow

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.data.backingUp.ImportingResults
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.importdata.csv.Spacer8
import com.oneSaver.legacy.utils.format
import com.oneSaver.navigation.CSVScreen
import com.oneSaver.navigation.navigation
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.GradientMysave
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.Red
import com.oneSaver.allStatus.userInterface.theme.White
import com.oneSaver.legacy.legacyOld.ui.theme.components.BackButton
import com.oneSaver.allStatus.userInterface.theme.components.MysaveDividerLine
import com.oneSaver.allStatus.userInterface.theme.components.OnboardingButton
import kotlinx.collections.immutable.persistentListOf

@SuppressLint("ComposeModifierMissing")
@Composable
fun ImportResultUI(
    result: ImportingResults,
    launchedFromOnboarding: Boolean,
    isManualCsvImport: Boolean = false,
    onTryAgain: (() -> Unit)? = null,
    onFinish: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Spacer(Modifier.height(16.dp))

        val nav = navigation()
        BackButton(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            nav.onBackPressed()
        }

        Spacer(Modifier.height(24.dp))

        val importSuccess = result.transactionsImported > 0 &&
                result.transactionsImported > result.rowsFound / 2
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = if (importSuccess) stringResource(R.string.success) else stringResource(R.string.failure),
            style = UI.typo.h2.style(
                fontWeight = FontWeight.Black,
                color = if (importSuccess) UI.colors.pureInverse else Red
            )
        )

        Spacer(Modifier.height(32.dp))

        val successPercent = if (result.rowsFound > 0) {
            (result.transactionsImported / result.rowsFound.toDouble()) * 100
        } else {
            0.0
        }

        SuccessSectionUI(
            result = result,
            successPercent = successPercent,
        )

        MysaveDividerLine(
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        FailedSectionUI(
            result = result,
            successPercent = successPercent,
        )

        // TODO: Implement "See failed imports"

        if (!isManualCsvImport) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = stringResource(R.string.csv_import_failed),
                color = UI.colors.pureInverse,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                onClick = {
                    nav.navigateTo(CSVScreen(launchedFromOnboarding = launchedFromOnboarding))
                }
            ) {
                Text(
                    text = stringResource(id = R.string.manual_csv_import),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Spacer8()

        OnboardingButton(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.finish),
            textColor = White,
            backgroundGradient = GradientMysave,
            hasNext = true,
            enabled = true
        ) {
            onFinish()
        }

        if (onTryAgain != null) {
            Spacer(Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick = onTryAgain,
                enabled = true
            ) {
                Text(text = stringResource(R.string.try_again))
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SuccessSectionUI(
    result: ImportingResults,
    successPercent: Double,
) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.imported),
            style = UI.typo.b1.style(
                color = Green,
                fontWeight = FontWeight.Black
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "${successPercent.format(2)}%",
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Normal
            )
        )

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.transactions_imported, result.transactionsImported),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.accounts_imported, result.accountsImported),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.categories_imported, result.categoriesImported),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun FailedSectionUI(
    result: ImportingResults,
    successPercent: Double,
) {
    Column {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.failed),
            style = UI.typo.b1.style(
                fontWeight = FontWeight.Black,
                color = Red
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "${(100 - successPercent).format(2)}%",
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Normal
            )
        )

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(
                R.string.rows_from_csv_not_recognized,
                result.rowsFound - result.transactionsImported
            ),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )
    }
}

@Preview(device = "id:pixel_3", showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    com.oneSaver.legacy.MySavePreview {
        ImportResultUI(
            result = ImportingResults(
                rowsFound = 356,
                transactionsImported = 320,
                accountsImported = 4,
                categoriesImported = 13,
                failedRows = persistentListOf()
            ),
            launchedFromOnboarding = false,
        ) {
        }
    }
}
