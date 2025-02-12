package com.oneSaver.transfers

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.oneSaver.userInterface.testing.PaparazziScreenshotTest
import com.oneSaver.userInterface.testing.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class TransactionsPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot Transactions Screen`() {
        snapshot(theme) {
            TransactionsUiTest(theme == PaparazziTheme.Dark)
        }
    }
}