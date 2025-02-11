package com.financeAndMoney.attributions

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.financeAndMoney.userInterface.testing.PaparazziScreenshotTest
import com.financeAndMoney.userInterface.testing.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class AttributionsScreenPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot Attribution Screen`() {
        snapshot(theme) {
            AttributionScreenUiTest(theme == PaparazziTheme.Dark)
        }
    }
}