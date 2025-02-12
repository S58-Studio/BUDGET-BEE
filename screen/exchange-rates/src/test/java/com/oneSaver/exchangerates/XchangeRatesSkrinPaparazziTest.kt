package com.oneSaver.exchangerates

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.oneSaver.userInterface.testing.PaparazziScreenshotTest
import com.oneSaver.userInterface.testing.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class XchangeRatesSkrinPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot Exchange Rates Screen`() {
        snapshot(theme) {
            ExchangeRatesScreenUiTest(theme == PaparazziTheme.Dark)
        }
    }
}