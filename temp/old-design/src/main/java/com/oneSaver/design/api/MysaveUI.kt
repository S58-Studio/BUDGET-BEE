package com.oneSaver.design.api

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.oneSaver.base.time.TimeConverter
import com.oneSaver.base.time.TimeProvider
import com.oneSaver.design.MysaveContext
import com.oneSaver.design.l0_system.MysaveTheme
import com.oneSaver.userInterface.time.TimeFormatter

val LocalMyFinancesContext = compositionLocalOf<MysaveContext> { error("No LocalIvyContext") }

@Suppress("CompositionLocalAllowlist")
@Deprecated("Used only for time migration to Instant. Never use it in new code!")
val LocalTimeConverter = compositionLocalOf<TimeConverter> { error("No LocalTimeConverter") }

@Suppress("CompositionLocalAllowlist")
@Deprecated("Used only for time migration to Instant. Never use it in new code!")
val LocalTimeProvider = compositionLocalOf<TimeProvider> { error("No LocalTimeProvider") }

@Suppress("CompositionLocalAllowlist")
@Deprecated("Used only for time migration to Instant. Never use it in new code!")
val LocalTimeFormatter = compositionLocalOf<TimeFormatter> { error("No LocalTimeFormatter") }

@SuppressLint("ComposeModifierMissing")
@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun MysaveUI(
    timeConverter: TimeConverter,
    timeProvider: TimeProvider,
    timeFormatter: TimeFormatter,
    design: MysaveDesign,
    includeSurface: Boolean = true,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val mysaveContext = design.context()

    CompositionLocalProvider(
        LocalMyFinancesContext provides mysaveContext,
        LocalTimeConverter provides timeConverter,
        LocalTimeProvider provides timeProvider,
        LocalTimeFormatter provides timeFormatter,
    ) {
        MysaveTheme (
            theme = mysaveContext.theme,
            design = design
        ) {
            WrapWithSurface(includeSurface = includeSurface) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    mysaveContext.screenWidth = with(LocalDensity.current) {
                        maxWidth.roundToPx()
                    }
                    mysaveContext.screenHeight = with(LocalDensity.current) {
                        maxHeight.roundToPx()
                    }

                    content()
                }
            }
        }
    }
}

@Composable
private fun WrapWithSurface(
    includeSurface: Boolean,
    content: @Composable () -> Unit,
) {
    if (includeSurface) {
        Surface {
            content()
        }
    } else {
        content()
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ivyContext(): MysaveContext {
    return LocalMyFinancesContext.current
}
