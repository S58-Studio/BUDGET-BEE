package com.oneSaver.allStatus.userInterface.theme.wallet

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySaveComponentPreview
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.legacy.mySaveCtx
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Composable
fun PeriodSelector(
    modifier: Modifier = Modifier,
    period: com.oneSaver.legacy.data.model.TimePeriod,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onShowChoosePeriodModal: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(2.dp, UI.colors.medium, UI.shapes.rFull),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        if (period.month != null) {
            mysaveIcon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onPreviousMonth()
                    }
                    .padding(all = 8.dp)
                    .rotate(-180f),
                icon = R.drawable.ic_arrow_right
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .height(48.dp)
                .defaultMinSize(minWidth = 48.dp)
                .clip(UI.shapes.rFull)
                .clickable {
                    onShowChoosePeriodModal()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            mysaveIcon(
                icon = R.drawable.ic_calendar,
                tint = UI.colors.pureInverse
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = period.toDisplayShort(mySaveCtx().startDayOfMonth),
                style = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(Modifier.weight(1f))

        if (period.month != null) {
            mysaveIcon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onNextMonth()
                    }
                    .padding(all = 8.dp),
                icon = R.drawable.ic_arrow_right
            )
        }

        Spacer(Modifier.width(20.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    MySaveComponentPreview {
        PeriodSelector(
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), // preview
            onPreviousMonth = { },
            onNextMonth = { }
        ) {
        }
    }
}
