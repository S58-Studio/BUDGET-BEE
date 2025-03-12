package com.oneSaver.iliyopangwa.editUpcomingPayments

import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.IntervalType
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import com.oneSaver.allStatus.userInterface.theme.modal.RecurringRuleModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AccountModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModalData
import java.time.LocalDateTime

sealed interface EditPlannedScreenEvents {
    data class OnRuleChanged(
        val startDate: LocalDateTime,
        val oneTime: Boolean,
        val intervalN: Int?,
        val intervalType: IntervalType?
    ) : EditPlannedScreenEvents

    data class OnAmountChanged(val newAmount: Double) : EditPlannedScreenEvents
    data class OnTitleChanged(val newTitle: String?) : EditPlannedScreenEvents
    data class OnDescriptionChanged(val newDescription: String?) : EditPlannedScreenEvents
    data class OnCategoryChanged(val newCategory: Category?) : EditPlannedScreenEvents
    data class OnAccountChanged(val newAccount: Account) : EditPlannedScreenEvents
    data class OnSetTransactionType(val newTransactionType: TransactionType) :
        EditPlannedScreenEvents

    data class OnSave(val closeScreen: Boolean = true) : EditPlannedScreenEvents
    data object OnDelete : EditPlannedScreenEvents
    data class OnEditCategory(val updatedCategory: Category) : EditPlannedScreenEvents
    data class OnCreateCategory(val data: CreateCategoryData) : EditPlannedScreenEvents
    data class OnCreateAccount(val data: CreateAccountData) : EditPlannedScreenEvents
    data class OnCategoryModalVisible(val visible: Boolean) : EditPlannedScreenEvents
    data class OnDescriptionModalVisible(val visible: Boolean) : EditPlannedScreenEvents
    data class OnDeleteTransactionModalVisible(val visible: Boolean) : EditPlannedScreenEvents
    data class OnAmountModalVisible(val visible: Boolean) : EditPlannedScreenEvents
    data class OnTransactionTypeModalVisible(val visible: Boolean) : EditPlannedScreenEvents
    data class OnCategoryModalDataChanged(val categoryModalData: CategoryModalData?) :
        EditPlannedScreenEvents

    data class OnRecurringRuleModalDataChanged(val recurringRuleModalData: RecurringRuleModalData?) :
        EditPlannedScreenEvents

    data class OnAccountModalDataChanged(val accountModalData: AccountModalData?) :
        EditPlannedScreenEvents
}
