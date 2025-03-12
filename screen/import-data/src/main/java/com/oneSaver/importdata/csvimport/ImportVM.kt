package com.oneSaver.importdata.csvimport

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oneSaver.frp.test.TestIdlingResource
import com.oneSaver.legacy.domain.deprecated.logic.csv.CSVImporter
import com.oneSaver.legacy.domain.deprecated.logic.csv.model.ImportType
import com.oneSaver.data.backingUp.BackingUpDataUseCase
import com.oneSaver.legacy.utils.asLiveData
import com.oneSaver.base.legacy.getFileName
import com.oneSaver.navigation.ImportingSkrin
import com.oneSaver.navigation.Navigation
import com.oneSaver.onboarding.viewmodel.OnboardingViewModel
import com.oneSaver.allStatus.domain.deprecated.logic.csv.CSVMapper
import com.oneSaver.allStatus.domain.deprecated.logic.csv.CSVNormalizer
import com.oneSaver.data.file.FileSystem
import com.oneSaver.data.backingUp.ImportingResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ImportVM @Inject constructor(
    private val ivyContext: com.oneSaver.legacy.MySaveCtx,
    private val nav: Navigation,
    private val fileReader: FileSystem,
    private val csvNormalizer: CSVNormalizer,
    private val csvMapper: CSVMapper,
    private val csvImporter: CSVImporter,
    private val backingUpDataUseCase: BackingUpDataUseCase
) : ViewModel() {
    private val _importingSteps = MutableLiveData<ImportingSteps>()
    val importStep = _importingSteps.asLiveData()

    private val _importType = MutableLiveData<ImportType>()
    val importType = _importType.asLiveData()

    private val _importProgressPercent = MutableLiveData<Int>()
    val importProgressPercent = _importProgressPercent.asLiveData()

    private val _importingResults = MutableLiveData<ImportingResults>()
    val importResult = _importingResults.asLiveData()

    fun start(screen: ImportingSkrin) {
        nav.onBackPressed[screen] = {
            when (importStep.value) {
                ImportingSteps.IMPORT_FROM -> false
                ImportingSteps.INSTRUCTIONS -> {
                    _importingSteps.value = ImportingSteps.IMPORT_FROM
                    true
                }

                ImportingSteps.LOADING -> {
                    // do nothing, disable back
                    true
                }

                ImportingSteps.RESULT -> {
                    _importingSteps.value = ImportingSteps.IMPORT_FROM
                    true
                }

                null -> false
            }
        }
    }

    @ExperimentalStdlibApi
    fun uploadFile(context: Context) {
        val importType = importType.value ?: return

        ivyContext.openFile { fileUri ->
            viewModelScope.launch {
                TestIdlingResource.increment()

                _importingSteps.value = ImportingSteps.LOADING

                _importingResults.value = if (hasCSVExtension(context, fileUri)) {
                    restoreCSVFile(fileUri = fileUri, importType = importType)
                } else {
                    backingUpDataUseCase.importBackupFile(
                        backupFileUri = fileUri
                    ) { progressPercent ->
                        com.oneSaver.legacy.utils.uiThread {
                            _importProgressPercent.value =
                                (progressPercent * 100).roundToInt()
                        }
                    }
                }

                _importingSteps.value = ImportingSteps.RESULT

                TestIdlingResource.decrement()
            }
        }
    }

    @ExperimentalStdlibApi
    private suspend fun restoreCSVFile(fileUri: Uri, importType: ImportType): ImportingResults {
        return com.oneSaver.legacy.utils.ioThread {
            val rawCSV = fileReader.read(
                uri = fileUri,
                charset = when (importType) {
                    ImportType.MYSAVE -> Charsets.UTF_16
                    else -> Charsets.UTF_8
                }
            ).getOrNull()
            if (rawCSV.isNullOrBlank()) {
                return@ioThread ImportingResults(
                    rowsFound = 0,
                    transactionsImported = 0,
                    accountsImported = 0,
                    categoriesImported = 0,
                    failedRows = persistentListOf()
                )
            }

            val normalizedCSV = csvNormalizer.normalize(
                rawCSV = rawCSV,
                importType = importType
            )

            val mapping = csvMapper.mapping(
                type = importType,
                headerRow = normalizedCSV.split("\n").getOrNull(0)
            )

            return@ioThread try {
                val result = csvImporter.import(
                    csv = normalizedCSV,
                    rowMapping = mapping,
                    onProgress = { progressPercent ->
                        com.oneSaver.legacy.utils.uiThread {
                            _importProgressPercent.value =
                                (progressPercent * 100).roundToInt()
                        }
                    }
                )

                if (result.failedRows.isNotEmpty()) {
                    Timber.e("Import failed rows: ${result.failedRows}")
                }

                result
            } catch (e: Exception) {
                e.printStackTrace()
                ImportingResults(
                    rowsFound = 0,
                    transactionsImported = 0,
                    accountsImported = 0,
                    categoriesImported = 0,
                    failedRows = persistentListOf()
                )
            }
        }
    }

    fun setImportType(importType: ImportType) {
        _importType.value = importType
        _importingSteps.value = ImportingSteps.INSTRUCTIONS
    }

    fun skip(
        screen: ImportingSkrin,
        onboardingViewModel: OnboardingViewModel
    ) {
        if (screen.launchedFromOnboarding) {
            onboardingViewModel.importSkip()
        }

        nav.back()
        resetState()
    }

    fun finish(
        screen: ImportingSkrin,
        onboardingViewModel: OnboardingViewModel
    ) {
        if (screen.launchedFromOnboarding) {
            val importSuccess = importResult.value?.transactionsImported?.let { it > 0 } ?: false
            onboardingViewModel.importFinished(
                success = importSuccess
            )
        }

        nav.back()
        resetState()
    }

    private fun resetState() {
        _importingSteps.value = ImportingSteps.IMPORT_FROM
    }

    private suspend fun hasCSVExtension(
        context: Context,
        fileUri: Uri
    ): Boolean = com.oneSaver.legacy.utils.ioThread {
        val fileName = context.getFileName(fileUri)
        fileName?.endsWith(suffix = ".csv", ignoreCase = true) ?: false
    }
}
