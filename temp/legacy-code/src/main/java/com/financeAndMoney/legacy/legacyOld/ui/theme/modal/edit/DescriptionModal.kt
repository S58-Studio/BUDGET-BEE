package com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.design.utils.hideKeyboard
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.rootView
import com.financeAndMoney.legacy.utils.clickableNoIndication
import com.financeAndMoney.legacy.utils.onScreenStart
import com.financeAndMoney.legacy.utils.rememberInteractionSource
import com.financeAndMoney.legacy.utils.selectEndTextFieldValue
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.IvyDescriptionTextField
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.MysaveModal
import com.financeAndMoney.legacy.legacyOld.ui.theme.modal.ModalDynamicPrimaryAction
import java.util.UUID

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Suppress("ParameterNaming")
@Composable
fun BoxWithConstraintsScope.DescriptionModal(
    id: UUID = UUID.randomUUID(),
    visible: Boolean,
    description: String?,

    onDescriptionChanged: (String?) -> Unit,
    dismiss: () -> Unit,
) {
    var descTextFieldValue by remember(description) {
        mutableStateOf(selectEndTextFieldValue(description))
    }
    val view = rootView()

    MysaveModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalDynamicPrimaryAction(
                initialEmpty = description.isNullOrBlank(),
                initialChanged = description != descTextFieldValue.text,

                testTagSave = "modal_desc_save",
                testTagDelete = "modal_desc_delete",

                onSave = {
                    onDescriptionChanged(descTextFieldValue.text)
                    view.hideKeyboard()
                },
                onDelete = {
                    onDescriptionChanged(null)
                    view.hideKeyboard()
                },
                dismiss = dismiss
            )
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .padding(start = 32.dp),
            text = stringResource(R.string.description),
            style = UI.typo.b1.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(24.dp))

        val focus = FocusRequester()
        onScreenStart {
            focus.requestFocus()
        }

        IvyDescriptionTextField(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .focusRequester(focus),
            testTag = "modal_desc_input",
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
                onAny = {
                    descTextFieldValue = descTextFieldValue.copy(
                        text = StringBuilder(descTextFieldValue.text)
                            .insert(descTextFieldValue.selection.end, "\n")
                            .toString(),
                        selection = TextRange(descTextFieldValue.selection.end + 1)
                    )
                }
            ),
            value = descTextFieldValue,
            hint = stringResource(R.string.description_text_field_hint),
        ) {
            descTextFieldValue = it
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clickableNoIndication(rememberInteractionSource()) {
                    focus.requestFocus()
                }
        )
    }
}

@Preview
@Composable
private fun PreviewDescriptionModal_emptyText() {
    MySavePreview {
        DescriptionModal(
            visible = true,
            description = "",
            onDescriptionChanged = {}
        ) {
        }
    }
}
