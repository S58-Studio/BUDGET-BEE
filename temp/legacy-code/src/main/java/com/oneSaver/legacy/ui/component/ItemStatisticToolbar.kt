package com.oneSaver.legacy.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.navigation.navigation
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Transparent
import com.oneSaver.legacy.legacyOld.ui.theme.components.CircleButton
import com.oneSaver.allStatus.userInterface.theme.components.DeleteButton
import com.oneSaver.allStatus.userInterface.theme.components.MysaveOutlinedButton

@SuppressLint("ComposeModifierMissing")
@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun ItemStatisticToolbar(
    contrastColor: Color,
    onEdit: () -> Unit,
    showEditButton: Boolean = true,
    showDeleteButton: Boolean = true,
    onDelete: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val nav = navigation()
        CircleButton(
            modifier = Modifier.testTag("toolbar_close"),
            icon = R.drawable.ic_dismiss,
            borderColor = contrastColor,
            tint = contrastColor,
            backgroundColor = Transparent
        ) {
            nav.back()
        }

        Spacer(Modifier.weight(1f))

        if (showEditButton) {
            MysaveOutlinedButton(
                iconStart = R.drawable.ic_edit,
                text = stringRes(R.string.edit),
                borderColor = contrastColor,
                iconTint = contrastColor,
                textColor = contrastColor,
                solidBackground = false
            ) {
                onEdit()
            }
        }

        Spacer(Modifier.width(16.dp))

        if (showDeleteButton) {
            DeleteButton {
                onDelete()
            }
        }

        Spacer(Modifier.width(24.dp))
    }
}
