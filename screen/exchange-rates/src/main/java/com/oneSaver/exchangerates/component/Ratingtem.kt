package com.oneSaver.exchangerates.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.design.l1_buildingBlocks.SpacerHor
import com.oneSaver.design.l1_buildingBlocks.SpacerWeight
import com.oneSaver.exchangerates.data.RatingUI
import com.oneSaver.legacy.MySaveComponentPreview
import com.oneSaver.legacy.utils.format
import com.oneSaver.allStatus.userInterface.theme.components.DeleteButton

@Composable
fun RateItem(
    rate: RatingUI,
    onDelete: (() -> Unit)?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, UI.colors.primary)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${rate.from}-${rate.to}:",
            style = UI.typo.nB1.style(
                fontWeight = FontWeight.Normal
            )
        )
        SpacerHor(width = 8.dp)
        Text(
            text = rate.rate.format(currencyCode = rate.to),
            style = UI.typo.nB1.style(
                fontWeight = FontWeight.SemiBold
            )
        )
        if (onDelete != null) {
            SpacerWeight(weight = 1f)
            DeleteButton(onClick = onDelete)
        }
    }
}

// region Preview
@Preview
@Composable
private fun Preview() {
    MySaveComponentPreview {
        RateItem(
            rate = RatingUI(
                from = "BGN",
                to = "EUR",
                rate = 1.95583
            ),
            onDelete = null,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Delete() {
    MySaveComponentPreview {
        RateItem(
            rate = RatingUI(
                from = "BGN",
                to = "EUR",
                rate = 1.95583
            ),
            onDelete = { },
            onClick = {}
        )
    }
}
// endregion
