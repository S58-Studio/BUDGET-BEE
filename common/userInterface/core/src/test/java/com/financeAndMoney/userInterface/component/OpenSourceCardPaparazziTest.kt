package com.financeAndMoney.userInterface.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.financeAndMoney.userInterface.PaparazziScreenshotTest
import com.financeAndMoney.userInterface.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class OpenSourceCardPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {

    @Test
    fun `default state`() {
        snapshot(theme) {
            Box(modifier = Modifier.padding(16.dp)) {
                IntroducingMyLonAppCard()
            }
        }
    }
}