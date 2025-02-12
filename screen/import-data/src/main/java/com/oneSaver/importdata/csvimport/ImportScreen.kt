package com.oneSaver.importdata.csvimport

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneSaver.importdata.csvimport.flow.ImportFrom
import com.oneSaver.importdata.csvimport.flow.ImportProcessing
import com.oneSaver.importdata.csvimport.flow.ImportResultUI
import com.oneSaver.importdata.csvimport.flow.masharti.ImportInstructions
import com.oneSaver.legacy.domain.deprecated.logic.csv.model.ImportType
import com.oneSaver.navigation.ImportingSkrin
import com.oneSaver.onboarding.viewmodel.OnboardingViewModel
import com.oneSaver.data.backingUp.ImportingResults

@OptIn(ExperimentalStdlibApi::class)
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ImportCSVScreen(screen: ImportingSkrin) {
    val viewModel: ImportVM = viewModel()

    val importingSteps by viewModel.importStep.observeAsState(ImportingSteps.IMPORT_FROM)
    val importType by viewModel.importType.observeAsState()
    val importProgressPercent by viewModel.importProgressPercent.observeAsState(0)
    val importResult by viewModel.importResult.observeAsState()

    val onboardingViewModel: OnboardingViewModel = viewModel()

    com.oneSaver.legacy.utils.onScreenStart {
        viewModel.start(screen)
    }
    val context = LocalContext.current

    UI(
        screen = screen,
        importingSteps = importingSteps,
        importType = importType,
        importProgressPercent = importProgressPercent,
        importingResults = importResult,

        onChooseImportType = viewModel::setImportType,
        onUploadCSVFile = { viewModel.uploadFile(context) },
        onSkip = {
            viewModel.skip(
                screen = screen,
                onboardingViewModel = onboardingViewModel
            )
        },
        onFinish = {
            viewModel.finish(
                screen = screen,
                onboardingViewModel = onboardingViewModel
            )
        }
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: ImportingSkrin,

    importingSteps: ImportingSteps,
    importType: ImportType?,
    importProgressPercent: Int,
    importingResults: ImportingResults?,

    onChooseImportType: (ImportType) -> Unit = {},
    onUploadCSVFile: () -> Unit = {},
    onSkip: () -> Unit = {},
    onFinish: () -> Unit = {},
) {
    when (importingSteps) {
        ImportingSteps.IMPORT_FROM -> {
            ImportFrom(
                hasSkip = screen.launchedFromOnboarding,
                launchedFromOnboarding = screen.launchedFromOnboarding,
                onSkip = onSkip,
                onImportFrom = onChooseImportType
            )
        }

        ImportingSteps.INSTRUCTIONS -> {
            ImportInstructions(
                hasSkip = screen.launchedFromOnboarding,
                importType = importType!!,
                onSkip = onSkip,
                onUploadClick = onUploadCSVFile
            )
        }

        ImportingSteps.LOADING -> {
            ImportProcessing(
                progressPercent = importProgressPercent
            )
        }

        ImportingSteps.RESULT -> {
            ImportResultUI(
                result = importingResults!!,
                launchedFromOnboarding = screen.launchedFromOnboarding,
            ) {
                onFinish()
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    com.oneSaver.legacy.MySavePreview {
        UI(
            screen = ImportingSkrin(launchedFromOnboarding = true),
            importingSteps = ImportingSteps.IMPORT_FROM,
            importType = null,
            importProgressPercent = 0,
            importingResults = null
        )
    }
}
