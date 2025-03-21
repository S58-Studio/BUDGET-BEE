package com.oneSaver.importdata.csvimport.flow.masharti

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oneSaver.core.userInterface.R

@Composable
fun DefaultImportSteps(
    videoUrl: String? = null,
    articleUrl: String? = null,

    onUploadClick: () -> Unit
) {
    Spacer(Modifier.height(12.dp))

    StepTitle(
        number = 1,
        title = stringResource(R.string.export_csv_file)
    )

    Spacer(Modifier.height(12.dp))

    Spacer(Modifier.height(24.dp))

    UploadFileStep(
        stepNumber = 2,
        onUploadClick = onUploadClick
    )
}
