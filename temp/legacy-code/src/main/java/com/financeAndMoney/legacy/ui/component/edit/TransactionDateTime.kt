package com.financeAndMoney.legacy.ui.component.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.MySaveComponentPreview
import com.financeAndMoney.legacy.utils.formatNicely
import com.financeAndMoney.legacy.utils.formatTimeOnly
import com.financeAndMoney.legacy.utils.timeNowLocal
import com.financeAndMoney.legacy.utils.timeNowUTC
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.mysaveIcon
import java.time.LocalDateTime

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun TransactionDateTime(
    dateTime: LocalDateTime?,
    dueDateTime: LocalDateTime?,
    onEditDate: () -> Unit,
    onEditTime: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (dueDateTime == null || dateTime != null) {
        Spacer(modifier.height(12.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(UI.shapes.r4)
                .background(UI.colors.medium, UI.shapes.r4)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))

            mysaveIcon(icon = R.drawable.ic_calendar)

            Spacer(Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.created_on),
                style = UI.typo.b2.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.width(24.dp))
            Spacer(Modifier.weight(1f))

            Text(
                text = (dateTime ?: timeNowUTC()).formatNicely(
                    noWeekDay = true
                ),
                style = UI.typo.nB2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.clickable {
                    onEditDate()
                }
            )
            Text(
                text = " " + (dateTime?.formatTimeOnly() ?: timeNowLocal().formatTimeOnly()),
                style = UI.typo.nB2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.clickable {
                    onEditTime()
                }
            )
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MySaveComponentPreview {
        TransactionDateTime(
            dateTime = timeNowUTC(),
            dueDateTime = null,
            onEditDate = {
            },
            onEditTime = {
            }
        )
    }
}
