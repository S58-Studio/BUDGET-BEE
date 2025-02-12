package com.oneSaver.report

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.oneSaver.reportStatements.ReportNoFilterUiTest
import com.oneSaver.reportStatements.ReportUiTest
import com.oneSaver.userInterface.testing.PaparazziScreenshotTest
import com.oneSaver.userInterface.testing.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ReportPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot Report Screen`() {
        snapshot(theme) {
            ReportUiTest(theme == PaparazziTheme.Dark)
        }
    }

    @Test
    fun `snapshot Report Screen no filter`() {
        snapshot(theme) {
            ReportNoFilterUiTest(theme == PaparazziTheme.Dark)
        }
    }
}