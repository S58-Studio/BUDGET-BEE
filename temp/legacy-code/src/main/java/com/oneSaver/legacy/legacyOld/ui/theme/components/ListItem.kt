package com.oneSaver.domains.legacy.ui.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.design.utils.thenIf
import com.oneSaver.allStatus.userInterface.theme.components.ItemIconSDefaultIcon
import com.oneSaver.allStatus.userInterface.theme.findContrastTextColor

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun ListItem(
    icon: String?,
    @DrawableRes defaultIcon: Int,
    text: String,
    selectedColor: Color?,
    onClick: (selected: Boolean) -> Unit
) {
    val textColor =
        if (selectedColor != null) findContrastTextColor(selectedColor) else UI.colors.pureInverse

    val medium = UI.colors.medium
    val rFull = UI.shapes.rFull

    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .thenIf(selectedColor == null) {
                border(2.dp, medium, rFull)
            }
            .thenIf(selectedColor != null) {
                background(selectedColor!!, rFull)
            }
            .clickable(
                onClick = {
                    onClick(selectedColor != null)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        ItemIconSDefaultIcon(
            iconName = icon,
            defaultIcon = defaultIcon,
            tint = textColor
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = text,
            style = UI.typo.b2.style(
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.width(12.dp))
}
