package com.oneSaver.transaction

import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.Tag
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed interface EditTransactionViewEvent {
    data class OnAmountChanged(val newAmount: Double) : EditTransactionViewEvent
    data class OnTitleChanged(val newTitle: String?) : EditTransactionViewEvent
    data class OnDescriptionChanged(val newDescription: String?) : EditTransactionViewEvent
    data class OnCategoryChanged(val newCategory: Category?) : EditTransactionViewEvent
    data class OnAccountChanged(val newAccount: Account) : EditTransactionViewEvent
    data class OnToAccountChanged(val newAccount: Account) : EditTransactionViewEvent
    data class OnDueDateChanged(val newDueDate: LocalDateTime?) : EditTransactionViewEvent
    data object OnChangeDate : EditTransactionViewEvent
    data object OnChangeTime : EditTransactionViewEvent
    data class OnSetTransactionType(val newTransactionType: TransactionType) :
        EditTransactionViewEvent

    data object OnPayPlannedPayment : EditTransactionViewEvent
    data object Delete : EditTransactionViewEvent
    data object Duplicate : EditTransactionViewEvent
    data class CreateCategory(val data: CreateCategoryData) : EditTransactionViewEvent
    data class EditCategory(val updatedCategory: Category) : EditTransactionViewEvent
    data class CreateAccount(val data: CreateAccountData) : EditTransactionViewEvent
    data class Save(val closeScreen: Boolean) : EditTransactionViewEvent
    data class SetHasChanges(val hasChangesValue: Boolean) : EditTransactionViewEvent
    data class UpdateExchangeRate(val exRate: Double?) : EditTransactionViewEvent

    sealed interface TagEvent : EditTransactionViewEvent {
        data class SaveTag(val name: String) : TagEvent
        data class OnTagSelect(val selectedTag: Tag) : TagEvent
        data class OnTagDeSelect(val selectedTag: Tag) : TagEvent
        data class OnTagDelete(val selectedTag: Tag) : TagEvent
        data class OnTagSearch(val query: String) : TagEvent
        data class OnTagEdit(val oldTag: Tag, val newTag: Tag) : TagEvent
    }
}
