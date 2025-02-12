package com.oneSaver.disclaimer

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.oneSaver.userInterface.testing.PaparazziScreenshotTest
import com.oneSaver.userInterface.testing.PaparazziTheme
import kotlinx.collections.immutable.toImmutableList
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DisclaimerScreenPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {

    @Test
    fun `none checked`() {
        snapshot(theme) {
            KanushoSkriniUI(
                viewState = KanushoViewState(
                    checkboxes = KanushoVM.LegalCheckboxes,
                    agreeButtonEnabled = false,
                ),
                onEvent = {}
            )
        }
    }

    @Test
    fun `all checked`() {
        snapshot(theme) {
            KanushoSkriniUI(
                viewState = KanushoViewState(
                    checkboxes = KanushoVM.LegalCheckboxes.map {
                        it.copy(checked = true)
                    }.toImmutableList(),
                    agreeButtonEnabled = true,
                ),
                onEvent = {}
            )
        }
    }
}