package com.oneSaver.iliyopangwa.editUpcomingPayments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.oneSaver.data.model.IntervalType
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySaveComponentPreview
import com.oneSaver.legacy.forDisplay
import com.oneSaver.legacy.utils.formatDateOnly
import com.oneSaver.legacy.utils.timeNowUTC
import com.oneSaver.legacy.utils.uppercaseLocal
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Orange
import com.oneSaver.allStatus.userInterface.theme.components.AddPrimaryAttributeButton
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import java.time.LocalDateTime

@Composable
fun RecurringRule(
    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,
    onShowRecurringRuleModal: () -> Unit,
) {
    if (
        hasRecurringRule(
            startDate = startDate,
            intervalN = intervalN,
            intervalType = intervalType,
            oneTime = oneTime
        )
    ) {
        RecurringRuleCard(
            startDate = startDate!!,
            intervalN = intervalN,
            intervalType = intervalType,
            oneTime = oneTime,
            onClick = {
                onShowRecurringRuleModal()
            }
        )
    } else {
        AddPrimaryAttributeButton(
            icon = R.drawable.ic_planned_payments,
            text = stringResource(R.string.add_planned_date_payment),
            onClick = onShowRecurringRuleModal
        )
    }
}

fun hasRecurringRule(
    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,
): Boolean {
    return startDate != null &&
        ((intervalN != null && intervalType != null) || oneTime)
}

@Composable
private fun RecurringRuleCard(
    startDate: LocalDateTime,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,
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

        Column {
            Text(
                text = if (oneTime) stringResource(R.string.planned_for) else stringResource(R.string.planned_start_at),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )

            if (!oneTime && intervalType != null && intervalN != null) {
                Spacer(Modifier.height(4.dp))

                val intervalTypeLabel = intervalType.forDisplay(intervalN).uppercaseLocal()
                Text(
                    text = stringResource(R.string.repeats_every, intervalN, intervalTypeLabel),
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = Orange
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = startDate.toLocalDate().formatDateOnly(),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Preview
@Composable
private fun Preview_Empty() {
    MySaveComponentPreview {
        RecurringRule(
            startDate = null,
            intervalN = null,
            intervalType = null,
            oneTime = true
        ) {
        }
    }
}

@Preview
@Composable
private fun Preview_Repeat() {
    MySaveComponentPreview {
        RecurringRule(
            startDate = timeNowUTC(),
            intervalN = 1,
            intervalType = IntervalType.MONTH,
            oneTime = false
        ) {
        }
    }
}

@Preview
@Composable
private fun Preview_OneTime() {
    MySaveComponentPreview {
        RecurringRule(
            startDate = timeNowUTC().plusDays(5),
            intervalN = null,
            intervalType = null,
            oneTime = true
        ) {
        }
    }
}
