package com.oneSaver.allStatus.userInterface.theme.modal

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.data.model.IntervalType
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.legacy.mySaveCtx
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.utils.addKeyboardListener
import com.oneSaver.legacy.utils.clickableNoIndication
import com.oneSaver.legacy.utils.closeDay
import com.oneSaver.legacy.utils.formatDateWeekDayLong
import com.oneSaver.legacy.utils.formatNicely
import com.oneSaver.legacy.utils.hideKeyboard
import com.oneSaver.legacy.utils.onScreenStart
import com.oneSaver.design.utils.thenIf
import com.oneSaver.legacy.utils.rememberInteractionSource
import com.oneSaver.legacy.utils.timeNowUTC
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.Gradient
import com.oneSaver.allStatus.userInterface.theme.GradientMysave
import com.oneSaver.allStatus.userInterface.theme.Gray
import com.oneSaver.allStatus.userInterface.theme.White
import com.oneSaver.allStatus.userInterface.theme.components.IntervalPickerRow
import com.oneSaver.legacy.legacyOld.ui.theme.components.MysaveCircleButton
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalSet
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalTitle
import com.oneSaver.allStatus.userInterface.theme.components.MysaveDividerLine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
data class RecurringRuleModalData(
    val initialStartDate: LocalDateTime?,
    val initialIntervalN: Int?,
    val initialIntervalType: IntervalType?,
    val initialOneTime: Boolean = false,
    val id: UUID = UUID.randomUUID()
)

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Suppress("ParameterNaming")
@Composable
fun BoxWithConstraintsScope.RecurringRuleModal(
    modal: RecurringRuleModalData?,

    dismiss: () -> Unit,
    onRuleChanged: (LocalDateTime, oneTime: Boolean, Int?, IntervalType?) -> Unit,
) {
    var startDate by remember(modal) {
        mutableStateOf(modal?.initialStartDate ?: timeNowUTC())
    }
    var oneTime by remember(modal) {
        mutableStateOf(modal?.initialOneTime ?: false)
    }
    var intervalN by remember(modal) {
        mutableStateOf(modal?.initialIntervalN ?: 1)
    }
    var intervalType by remember(modal) {
        mutableStateOf(modal?.initialIntervalType ?: IntervalType.MONTH)
    }

    val modalScrollState = rememberScrollState()

    MysaveModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        scrollState = modalScrollState,
        PrimaryAction = {
            ModalSet(
                modifier = Modifier.testTag("recurringModalSet"),
                enabled = validate(oneTime, intervalN, intervalType)
            ) {
                dismiss()
                onRuleChanged(
                    startDate,
                    oneTime,
                    intervalN,
                    intervalType
                )
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        val rootView = LocalView.current
        onScreenStart {
            hideKeyboard(rootView)
        }

        ModalTitle(text = stringResource(R.string.plan_for))

        Spacer(Modifier.height(16.dp))

        // One-time & Multiple Times
        TimesSelector(oneTime = oneTime) {
            oneTime = it
        }

        if (oneTime) {
            OneTime(
                date = startDate,
                onDatePicked = {
                    startDate = it
                }
            )
        } else {
            MultipleTimes(
                startDate = startDate,
                intervalN = intervalN,
                intervalType = intervalType,

                modalScrollState = modalScrollState,

                onSetStartDate = {
                    startDate = it
                },
                onSetIntervalN = {
                    intervalN = it
                },
                onSetIntervalType = {
                    intervalType = it
                }
            )
        }
    }
}

private fun validate(
    oneTime: Boolean,
    intervalN: Int?,
    intervalType: IntervalType?
): Boolean {
    return oneTime || intervalN != null && intervalN > 0 && intervalType != null
}

@Composable
private fun TimesSelector(
    oneTime: Boolean,

    onSetOneTime: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .background(UI.colors.medium, UI.shapes.r2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))

        TimesSelectorButton(
            selected = oneTime,
            label = stringResource(R.string.one_time)
        ) {
            onSetOneTime(true)
        }

        Spacer(Modifier.width(8.dp))

        TimesSelectorButton(
            selected = !oneTime,
            label = stringResource(R.string.multiple_times)
        ) {
            onSetOneTime(false)
        }

        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun RowScope.TimesSelectorButton(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    val rFull = UI.shapes.rFull

    Text(
        modifier = Modifier
            .weight(1f)
            .clip(UI.shapes.rFull)
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp)
            .thenIf(selected) {
                background(GradientMysave.asHorizontalBrush(), rFull)
            }
            .padding(vertical = 8.dp),
        text = label,
        style = UI.typo.b2.style(
            color = if (selected) White else Gray,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
@Suppress("ParameterNaming")
private fun OneTime(
    date: LocalDateTime,
    onDatePicked: (LocalDateTime) -> Unit
) {
    Spacer(Modifier.height(44.dp))

    DateRow(dateTime = date) {
        onDatePicked(it)
    }

    Spacer(Modifier.height(64.dp))
}

@Composable
private fun MultipleTimes(
    startDate: LocalDateTime,
    intervalN: Int,
    intervalType: IntervalType,

    modalScrollState: ScrollState,

    onSetStartDate: (LocalDateTime) -> Unit,
    onSetIntervalN: (Int) -> Unit,
    onSetIntervalType: (IntervalType) -> Unit
) {
    Spacer(Modifier.height(40.dp))

    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = stringResource(R.string.starts_on),
        style = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(12.dp))

    DateRow(dateTime = startDate) {
        onSetStartDate(it)
    }

    Spacer(Modifier.height(32.dp))

    MysaveDividerLine(
        modifier = Modifier.padding(horizontal = 24.dp)
    )

    Spacer(Modifier.height(32.dp))

    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = stringResource(R.string.repeats_every_text),
        style = UI.typo.b2.style(
            fontWeight = FontWeight.ExtraBold,
            color = UI.colors.pureInverse
        )
    )

    Spacer(Modifier.height(16.dp))

    val rootView = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    onScreenStart {
        rootView.addKeyboardListener { keyboardShown ->
            if (keyboardShown) {
                coroutineScope.launch {
                    delay(200)
                    modalScrollState.animateScrollTo(modalScrollState.maxValue)
                }
            }
        }
    }

    IntervalPickerRow(
        intervalN = intervalN,
        intervalType = intervalType,
        onSetIntervalN = onSetIntervalN,
        onSetIntervalType = onSetIntervalType
    )

    Spacer(Modifier.height(48.dp))
}

@Composable
@Suppress("ParameterNaming")
private fun DateRow(
    dateTime: LocalDateTime,
    onDatePicked: (LocalDateTime) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(32.dp))

        val ivyContext = mySaveCtx()

        Column(
            modifier = Modifier.clickableNoIndication(rememberInteractionSource()) {
                ivyContext.pickDate(dateTime.toLocalDate(), onDatePicked)
            }
        ) {
            val date = dateTime.toLocalDate()
            val closeDay = date.closeDay()

            Text(
                text = closeDay ?: date.formatNicely(
                    pattern = "EEEE, dd MMM"
                ),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Normal,
                    color = UI.colors.pureInverse
                )
            )

            if (closeDay != null) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = date.formatDateWeekDayLong(),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.SemiBold,
                        color = Gray
                    )
                )
            }
        }

        Spacer(Modifier.width(24.dp))
        Spacer(Modifier.weight(1f))

        MysaveCircleButton(
            modifier = Modifier
                .size(48.dp)
                .testTag("recurring_modal_pick_date"),
            backgroundPadding = 4.dp,
            icon = R.drawable.ic_calendar,
            backgroundGradient = Gradient.solid(UI.colors.pureInverse),
            tint = UI.colors.pure
        ) {
            ivyContext.pickDate(dateTime.toLocalDate(), onDatePicked)
        }

        Spacer(Modifier.width(32.dp))
    }
}

private fun MySaveCtx.pickDate(
    initialDate: LocalDate,
    onDatePicked: (
        LocalDateTime
    ) -> Unit
) {
    datePicker(
        initialDate = initialDate
    ) {
        onDatePicked(it.atTime(12, 0))
    }
}

@Preview
@Composable
private fun Preview_oneTime() {
    MySavePreview {
        BoxWithConstraints(Modifier.padding(bottom = 48.dp)) {
            RecurringRuleModal(
                modal = RecurringRuleModalData(
                    initialStartDate = null,
                    initialIntervalN = null,
                    initialIntervalType = null,
                    initialOneTime = true
                ),
                dismiss = {},
                onRuleChanged = { _, _, _, _ -> }
            )
        }
    }
}

@Preview
@Composable
private fun Preview_multipleTimes() {
    MySavePreview {
        BoxWithConstraints(Modifier.padding(bottom = 48.dp)) {
            RecurringRuleModal(
                modal = RecurringRuleModalData(
                    initialStartDate = null,
                    initialIntervalN = null,
                    initialIntervalType = null,
                    initialOneTime = false
                ),
                dismiss = {},
                onRuleChanged = { _, _, _, _ -> }
            )
        }
    }
}
