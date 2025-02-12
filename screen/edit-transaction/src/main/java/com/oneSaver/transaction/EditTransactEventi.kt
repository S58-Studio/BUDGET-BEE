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

sealed interface EditTransactEventi {
    data class OnAmountChanged(val newAmount: Double) : EditTransactEventi
    data class OnTitleChanged(val newTitle: String?) : EditTransactEventi
    data class OnDescriptionChanged(val newDescription: String?) : EditTransactEventi
    data class OnCategoryChanged(val newCategory: Category?) : EditTransactEventi
    data class OnAccountChanged(val newAccount: Account) : EditTransactEventi
    data class OnToAccountChanged(val newAccount: Account) : EditTransactEventi
    data class OnDueDateChanged(val newDueDate: LocalDateTime?) : EditTransactEventi
    data class OnSetDateTime(val newDateTime: LocalDateTime) : EditTransactEventi
    data class OnSetDate(val newDate: LocalDate) : EditTransactEventi
    data class OnSetTime(val newTime: LocalTime) : EditTransactEventi
    data class OnSetTransactionType(val newTransactionType: TransactionType) : EditTransactEventi
    data object OnPayPlannedPayment : EditTransactEventi
    data object Delete : EditTransactEventi
    data object Duplicate : EditTransactEventi
    data class CreateCategory(val data: CreateCategoryData) : EditTransactEventi
    data class EditCategory(val updatedCategory: Category) : EditTransactEventi
    data class CreateAccount(val data: CreateAccountData) : EditTransactEventi
    data class Save(val closeScreen: Boolean) : EditTransactEventi
    data class SetHasChanges(val hasChangesValue: Boolean) : EditTransactEventi
    data class UpdateExchangeRate(val exRate: Double?) : EditTransactEventi

    sealed interface TagEvent : EditTransactEventi {
        data class SaveTag(val name: String) : TagEvent
        data class OnTagSelect(val selectedTag: Tag) : TagEvent
        data class OnTagDeSelect(val selectedTag: Tag) : TagEvent
        data class OnTagDelete(val selectedTag: Tag) : TagEvent
        data class OnTagSearch(val query: String) : TagEvent
        data class OnTagEdit(val oldTag: Tag, val newTag: Tag) : TagEvent
    }
}
