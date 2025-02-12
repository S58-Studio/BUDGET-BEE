package com.oneSaver.legacy.legacyOld.ui.theme.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oneSaver.legacy.mySaveCtx
import com.oneSaver.legacy.utils.convertUTCtoLocal
import com.oneSaver.legacy.utils.formatLocalTime
import com.oneSaver.legacy.utils.formatNicely
import com.oneSaver.legacy.utils.getTrueDate
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.components.MysaveOutlinedButton
import java.time.LocalDateTime

@Composable
fun DateTimeRow(
    dateTime: LocalDateTime,
    onSetDateTime: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val mySaveContext = mySaveCtx()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        MysaveOutlinedButton(
            text = dateTime.formatNicely(),
            iconStart = R.drawable.ic_date
        ) {
            mySaveContext.datePicker(
                initialDate = dateTime.convertUTCtoLocal().toLocalDate()
            ) {
                onSetDateTime(getTrueDate(it, dateTime.toLocalTime()))
            }
        }

        Spacer(Modifier.weight(1f))

        MysaveOutlinedButton(
            text = dateTime.formatLocalTime(),
            iconStart = R.drawable.ic_date
        ) {
            mySaveContext.timePicker {
                onSetDateTime(getTrueDate(dateTime.convertUTCtoLocal().toLocalDate(), it))
            }
        }

        Spacer(Modifier.width(24.dp))
    }
}