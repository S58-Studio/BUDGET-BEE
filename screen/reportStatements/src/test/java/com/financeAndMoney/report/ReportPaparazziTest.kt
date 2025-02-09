package com.financeAndMoney.report

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.financeAndMoney.reportStatements.ReportNoFilterUiTest
import com.financeAndMoney.reportStatements.ReportUiTest
import com.financeAndMoney.userInterface.testing.PaparazziScreenshotTest
import com.financeAndMoney.userInterface.testing.PaparazziTheme
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