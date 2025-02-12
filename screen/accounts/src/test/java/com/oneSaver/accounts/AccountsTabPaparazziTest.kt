package com.oneSaver.accounts

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.oneSaver.userInterface.testing.PaparazziScreenshotTest
import com.oneSaver.userInterface.testing.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class AccountsTabPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot accountTab composable`() {
        snapshot(theme) {
            AccountsTabUITest(theme == PaparazziTheme.Dark)
        }
    }
}