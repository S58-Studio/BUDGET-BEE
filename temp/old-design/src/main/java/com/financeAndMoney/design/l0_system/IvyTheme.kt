package com.financeAndMoney.design.l0_system

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.design.api.IvyDesign
import com.financeAndMoney.design.system.MysaveMaterial3Theme

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
val LocalIvyColors = compositionLocalOf<IvyColors> { error("No MysaveColors") }

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
val LocalIvyTypography = compositionLocalOf<IvyTypography> { error("No IvyTypography") }

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
val LocalIvyShapes = compositionLocalOf<IvyShapes> { error("No IvyShapes") }

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
object UI {
    val colors: IvyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyColors.current

    val typo: IvyTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyTypography.current

    val shapes: IvyShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyShapes.current
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun IvyTheme(
    theme: Theme,
    design: IvyDesign,
    content: @Composable () -> Unit
) {
    val colors = design.colors(theme, isSystemInDarkTheme())
    val typography = design.typography()
    val shapes = design.shapes()

    CompositionLocalProvider(
        LocalIvyColors provides colors,
        LocalIvyTypography provides typography,
        LocalIvyShapes provides shapes
    ) {
        val view = LocalView.current
        if (!view.isInEditMode && view.context is Activity) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    colors.isLight
            }
        }

        MysaveMaterial3Theme(
            dark = !colors.isLight,
            content = content,
        )
    }
}
