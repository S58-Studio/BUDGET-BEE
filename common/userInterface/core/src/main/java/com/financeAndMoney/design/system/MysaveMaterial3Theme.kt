package com.financeAndMoney.design.system

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.financeAndMoney.design.system.colors.MysaveColors

@Composable
fun MysaveMaterial3Theme(
    isTrueBlack: Boolean,
    dark: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (dark) mysaveDarkColorScheme(isTrueBlack) else mySaveLightColorScheme(),
        content = content,
    )
}

private fun mySaveLightColorScheme(): ColorScheme = ColorScheme(
    primary = MysaveColors.Purple.primary,
    onPrimary = MysaveColors.White,
    primaryContainer = MysaveColors.Purple.light,
    onPrimaryContainer = MysaveColors.White,
    inversePrimary = MysaveColors.Purple.dark,
    secondary = MysaveColors.Green.primary,
    onSecondary = MysaveColors.White,
    secondaryContainer = MysaveColors.Green.light,
    onSecondaryContainer = MysaveColors.White,
    tertiary = MysaveColors.Green.primary,
    onTertiary = MysaveColors.White,
    tertiaryContainer = MysaveColors.Green.light,
    onTertiaryContainer = MysaveColors.White,

    error = MysaveColors.Red.primary,
    onError = MysaveColors.White,
    errorContainer = MysaveColors.Red.light,
    onErrorContainer = MysaveColors.White,

    background = MysaveColors.White,
    onBackground = MysaveColors.Black,
    surface = MysaveColors.White,
    onSurface = MysaveColors.Black,
    surfaceVariant = MysaveColors.ExtraLightGray,
    onSurfaceVariant = MysaveColors.Black,
    surfaceTint = MysaveColors.Black,
    inverseSurface = MysaveColors.DarkGray,
    inverseOnSurface = MysaveColors.White,

    outline = MysaveColors.Gray,
    outlineVariant = MysaveColors.DarkGray,
    scrim = MysaveColors.ExtraDarkGray.copy(alpha = 0.8f)
)

private fun mysaveDarkColorScheme(isTrueBlack: Boolean): ColorScheme = ColorScheme(
    primary = MysaveColors.Purple.primary,
    onPrimary = MysaveColors.White,
    primaryContainer = MysaveColors.Purple.light,
    onPrimaryContainer = MysaveColors.White,
    inversePrimary = MysaveColors.Purple.dark,
    secondary = MysaveColors.Green.primary,
    onSecondary = MysaveColors.White,
    secondaryContainer = MysaveColors.Green.light,
    onSecondaryContainer = MysaveColors.White,
    tertiary = MysaveColors.Green.primary,
    onTertiary = MysaveColors.White,
    tertiaryContainer = MysaveColors.Green.light,
    onTertiaryContainer = MysaveColors.White,

    error = MysaveColors.Red.primary,
    onError = MysaveColors.White,
    errorContainer = MysaveColors.Red.light,
    onErrorContainer = MysaveColors.White,

    background = MysaveColors.Black,
    onBackground = MysaveColors.White,
    surface = MysaveColors.Black,
    onSurface = MysaveColors.White,
    surfaceVariant = MysaveColors.ExtraDarkGray,
    onSurfaceVariant = MysaveColors.White,
    surfaceTint = MysaveColors.White,
    inverseSurface = MysaveColors.LightGray,
    inverseOnSurface = MysaveColors.Black,

    outline = MysaveColors.Gray,
    outlineVariant = MysaveColors.LightGray,
    scrim = MysaveColors.ExtraLightGray.copy(alpha = 0.8f)
)
