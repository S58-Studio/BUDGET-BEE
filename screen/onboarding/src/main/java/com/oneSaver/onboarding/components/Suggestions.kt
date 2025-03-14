package com.oneSaver.onboarding.components

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.Purple
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySaveComponentPreview
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.utils.drawColoredShadow
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import com.oneSaver.allStatus.userInterface.theme.Green
import com.oneSaver.allStatus.userInterface.theme.Red
import com.oneSaver.allStatus.userInterface.theme.components.mysaveIcon
import com.oneSaver.allStatus.userInterface.theme.components.WrapContentRow

@Composable
fun Suggestions(
    suggestions: List<Any>,

    onAddSuggestion: (Any) -> Unit,
    onAddNew: () -> Unit
) {
    val items = suggestions.plus(AddNew())

    WrapContentRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        items = items,
        horizontalMarginBetweenItems = 8.dp,
        verticalMarginBetweenRows = 12.dp
    ) {
        when (it) {
            is CreateAccountData -> {
                Suggestion(name = it.name) {
                    onAddSuggestion(it)
                }
            }

            is CreateCategoryData -> {
                Suggestion(name = it.name) {
                    onAddSuggestion(it)
                }
            }

            is AddNew -> {
                AddNewButton {
                    onAddNew()
                }
            }
        }
    }
}

@Composable
private fun Suggestion(
    name: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .background(UI.colors.medium, UI.shapes.rFull)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        mysaveIcon(icon = R.drawable.ic_plus)

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = name,
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun AddNewButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .drawColoredShadow(color = UI.colors.mediumInverse)
            .clip(UI.shapes.rFull)
            .background(UI.colors.mediumInverse, UI.shapes.rFull)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        mysaveIcon(
            icon = R.drawable.ic_plus,
            tint = UI.colors.pure,
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = stringResource(R.string.add_new),
            style = UI.typo.b2.style(
                color = UI.colors.pure,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.width(32.dp))
    }
}

private class AddNew

@Preview
@Composable
private fun Preview() {
    MySaveComponentPreview {
        Suggestions(
            suggestions = listOf(
                Account("Cash", Green.toArgb()),
                Account("Bank", Red.toArgb()),
                Account("Revolut", Purple.toArgb())
            ),
            onAddSuggestion = { }
        ) {
        }
    }
}
