package com.financeAndMoney.design.api

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.financeAndMoney.design.MysaveContext
import com.financeAndMoney.design.l0_system.IvyTheme

val LocalMysaveContext = compositionLocalOf<MysaveContext> { error("No LocalMysaveContext") }

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun MysaveUI(
    design: IvyDesign,
    includeSurface: Boolean = true,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val ivyContext = design.context()

    CompositionLocalProvider(
        LocalMysaveContext provides ivyContext,
    ) {
        IvyTheme(
            theme = ivyContext.theme,
            design = design
        ) {
            WrapWithSurface(includeSurface = includeSurface) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    ivyContext.screenWidth = with(LocalDensity.current) {
                        maxWidth.roundToPx()
                    }
                    ivyContext.screenHeight = with(LocalDensity.current) {
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

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun ivyContext(): MysaveContext {
    return LocalMysaveContext.current
}
