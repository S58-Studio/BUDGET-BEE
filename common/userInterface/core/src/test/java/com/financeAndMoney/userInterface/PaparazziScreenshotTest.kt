package com.financeAndMoney.userInterface

import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.financeAndMoney.design.system.MysaveMaterial3Theme
import org.junit.Rule

open class PaparazziScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6_PRO,
        showSystemUi = true,
        maxPercentDifference = 0.001
    )

    protected fun snapshot(theme: PaparazziTheme, content: @Composable () -> Unit) {
        paparazzi.snapshot {
            MysaveMaterial3Theme(
                dark = when (theme) {
                    PaparazziTheme.Light -> false
                    PaparazziTheme.Dark -> true
                }
            ) {
                content()
            }
        }
    }
}

enum class PaparazziTheme {
    Light, Dark
}