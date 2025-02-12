package com.oneSaver.transfers

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.data.model.Category
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.navigation.TransactScrin
import com.oneSaver.allStatus.userInterface.theme.modal.ChoosePeriodModalData

sealed interface TransfersEvent {
    data class SetUpcomingExpanded(val expanded: Boolean) : TransfersEvent
    data class SetOverdueExpanded(val expanded: Boolean) : TransfersEvent

    data class SetPeriod(
        val screen: TransactScrin,
        val period: TimePeriod
    ) : TransfersEvent

    data class NextMonth(val screen: TransactScrin) : TransfersEvent
    data class PreviousMonth(val screen: TransactScrin) : TransfersEvent
    data class Delete(val screen: TransactScrin) : TransfersEvent
    data class EditCategory(val updatedCategory: Category) : TransfersEvent
    data class EditAccount(
        val screen: TransactScrin,
        val account: Account,
        val newBalance: Double
    ) : TransfersEvent

    data class PayOrGet(
        val screen: TransactScrin,
        val transaction: Transaction
    ) : TransfersEvent

    data class SkipTransaction(
        val screen: TransactScrin,
        val transaction: Transaction
    ) : TransfersEvent

    data class SkipTransfers(
        val screen: TransactScrin,
        val transactions: List<Transaction>
    ) : TransfersEvent

    data class UpdateAccountDeletionState(val confirmationText: String) : TransfersEvent
    data class SetSkipAllModalVisible(val visible: Boolean) : TransfersEvent
    data class OnDeleteModal1Visible(val delete: Boolean) : TransfersEvent
    data class OnChoosePeriodModalData(val data: ChoosePeriodModalData?) : TransfersEvent
}
