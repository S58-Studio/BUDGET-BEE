package com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.design.l0_system.UI
import com.financeAndMoney.design.l0_system.style
import com.financeAndMoney.legacy.MySavePreview
import com.financeAndMoney.legacy.legacyOld.ui.theme.modal.ModalAdd
import com.financeAndMoney.legacy.utils.onScreenStart
import com.financeAndMoney.legacy.utils.selectEndTextFieldValue
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.components.IvyTitleTextField
import java.util.UUID

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Suppress("ParameterNaming")
@Composable
fun BoxWithConstraintsScope.AddKeywordModal(
    id: UUID = UUID.randomUUID(),
    keyword: String,
    visible: Boolean,
    dismiss: () -> Unit,
    onKeywordChanged: (String) -> Unit
) {
    var modalKeyword by remember { mutableStateOf(selectEndTextFieldValue(keyword)) }

    MysaveModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalAdd {
                onKeywordChanged(modalKeyword.text)
                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(R.string.add_keyword),
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.height(32.dp))

        val inputFocus = FocusRequester()

        onScreenStart {
            inputFocus.requestFocus()
        }

        IvyTitleTextField(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .focusRequester(inputFocus),
            dividerModifier = Modifier.padding(horizontal = 24.dp),
            value = modalKeyword,
            hint = stringResource(R.string.keyword)
        ) {
            modalKeyword = it
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    MySavePreview {
        AddKeywordModal(
            visible = true,
            keyword = "",
            dismiss = {}
        ) {
        }
    }
}
