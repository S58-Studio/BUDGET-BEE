package com.oneSaver.allStatus.userInterface.edit.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySaveComponentPreview
import com.oneSaver.legacy.utils.formatDateOnly
import com.oneSaver.legacy.utils.timeNowUTC
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import java.time.LocalDateTime

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun DueDate(
    dueDate: LocalDateTime,
    onPickDueDate: () -> Unit,
) {
    DueDateCard(
        dueDate = dueDate,
        onClick = {
            onPickDueDate()
        }
    )
}

@Composable
private fun DueDateCard(
    dueDate: LocalDateTime,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .background(UI.colors.medium, UI.shapes.r4)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        mysaveIcon(icon = R.drawable.ic_planned_payments)

        Spacer(Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.planned_for),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = dueDate.toLocalDate().formatDateOnly(),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Preview
@Composable
private fun Preview_OneTime() {
    MySaveComponentPreview {
        DueDate(
            dueDate = timeNowUTC().plusDays(5),
        ) {
        }
    }
}
