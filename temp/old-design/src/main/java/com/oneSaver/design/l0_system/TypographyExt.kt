package com.oneSaver.design.l0_system

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
fun TextStyle.colorAs(color: Color) = this.copy(color = color)

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun TextStyle.style(
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Start
) = this.copy(
    color = color,
    fontWeight = fontWeight,
    textAlign = textAlign
)
