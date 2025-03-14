package com.oneSaver.legacy.legacyOld.ui.theme.modal

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oneSaver.core.userInterface.R
import com.oneSaver.design.l0_system.UI
import com.oneSaver.allStatus.userInterface.theme.components.BufferBattery
import com.oneSaver.allStatus.userInterface.theme.modal.ModalAmountSection
import com.oneSaver.allStatus.userInterface.theme.modal.MysaveModal
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AmountModal
import java.util.*

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
data class BufferModalData(
    val balance: Double,
    val buffer: Double,
    val currency: String,
    val id: UUID = UUID.randomUUID()
)

@Deprecated("Old design system. Use `:oneSaver-design` and Material3")
@Suppress("ParameterNaming")
@Composable
fun BoxWithConstraintsScope.BufferModal(
    modal: BufferModalData?,
    dismiss: () -> Unit,
    onBufferChanged: (Double) -> Unit
) {
    var newBufferAmount by remember(modal) {
        mutableStateOf(modal?.buffer ?: 0.0)
    }

    var amountModalVisible by remember { mutableStateOf(false) }

    MysaveModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSave {
                onBufferChanged(newBufferAmount)
                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(16.dp))

        BufferBattery(
            modifier = Modifier.padding(horizontal = 16.dp),
            buffer = newBufferAmount,
            balance = modal?.balance ?: 0.0,
            currency = modal?.currency ?: "",
            backgroundNotFilled = UI.colors.medium,
        )

        Spacer(Modifier.height(24.dp))

        ModalAmountSection(
            label = stringResource(R.string.edit_savings_goal),
            currency = modal?.currency ?: "",
            amount = newBufferAmount
        ) {
            amountModalVisible = true
        }
    }

    val amountModalId = remember(modal, newBufferAmount) {
        UUID.randomUUID()
    }
    AmountModal(
        id = amountModalId,
        visible = amountModalVisible,
        currency = modal?.currency ?: "",
        initialAmount = newBufferAmount,
        dismiss = { amountModalVisible = false }
    ) {
        newBufferAmount = it
    }
}
