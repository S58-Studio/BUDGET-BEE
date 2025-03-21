package com.oneSaver.allStatus.userInterface.theme.modal

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oneSaver.design.l0_system.UI
import com.oneSaver.design.l0_system.style
import com.oneSaver.legacy.MySavePreview
import com.oneSaver.legacy.legacyOld.ui.theme.modal.ModalSave
import com.oneSaver.legacy.utils.selectEndTextFieldValue
import com.oneSaver.core.userInterface.R
import com.oneSaver.allStatus.userInterface.theme.components.IvyTitleTextField
import java.util.UUID

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Suppress("ParameterNaming")
@Composable
fun BoxWithConstraintsScope.NameModal(
    visible: Boolean,
    name: String,
    dismiss: () -> Unit,
    id: UUID = UUID.randomUUID(),
    onNameChanged: (String) -> Unit
) {
    var modalName by remember(id) { mutableStateOf(selectEndTextFieldValue(name)) }

    MysaveModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSave {
                onNameChanged(modalName.text)
                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(R.string.edit_name),
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.height(32.dp))

        IvyTitleTextField(
            modifier = Modifier.padding(horizontal = 32.dp),
            dividerModifier = Modifier.padding(horizontal = 24.dp),
            value = modalName,
            hint = stringResource(R.string.what_is_your_name)
        ) {
            modalName = it
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    MySavePreview {
        NameModal(
            visible = true,
            name = "Iliyan",
            dismiss = {}
        ) {
        }
    }
}
