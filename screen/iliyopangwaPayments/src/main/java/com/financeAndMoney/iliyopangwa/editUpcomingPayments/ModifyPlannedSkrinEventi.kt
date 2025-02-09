package com.financeAndMoney.iliyopangwa.editUpcomingPayments

import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.data.model.IntervalType
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateAccountData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateCategoryData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.RecurringRuleModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.AccountModalData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.CategoryModalData
import java.time.LocalDateTime

sealed interface ModifyPlannedSkrinEventi {
    data class OnRuleChanged(
        val startDate: LocalDateTime,
        val oneTime: Boolean,
        val intervalN: Int?,
        val intervalType: IntervalType?
    ) : ModifyPlannedSkrinEventi

    data class OnAmountChanged(val newAmount: Double) : ModifyPlannedSkrinEventi
    data class OnTitleChanged(val newTitle: String?) : ModifyPlannedSkrinEventi
    data class OnDescriptionChanged(val newDescription: String?) : ModifyPlannedSkrinEventi
    data class OnCategoryChanged(val newCategory: Category?) : ModifyPlannedSkrinEventi
    data class OnAccountChanged(val newAccount: Account) : ModifyPlannedSkrinEventi
    data class OnSetTransactionType(val newTransactionType: TransactionType) :
        ModifyPlannedSkrinEventi

    data class OnSave(val closeScreen: Boolean = true) : ModifyPlannedSkrinEventi
    data object OnDelete : ModifyPlannedSkrinEventi
    data class OnModifyCategory(val updatedCategory: Category) : ModifyPlannedSkrinEventi
    data class OnCreateCategory(val data: CreateCategoryData) : ModifyPlannedSkrinEventi
    data class OnCreateAccount(val data: CreateAccountData) : ModifyPlannedSkrinEventi
    data class OnCategoryModalVisible(val visible: Boolean) : ModifyPlannedSkrinEventi
    data class OnDescriptionModalVisible(val visible: Boolean) : ModifyPlannedSkrinEventi
    data class OnDeleteTransactionModalVisible(val visible: Boolean) : ModifyPlannedSkrinEventi
    data class OnAmountModalVisible(val visible: Boolean) : ModifyPlannedSkrinEventi
    data class OnTransactionTypeModalVisible(val visible: Boolean) : ModifyPlannedSkrinEventi
    data class OnCategoryModalDataChanged(val categoryModalData: CategoryModalData?) :
        ModifyPlannedSkrinEventi

    data class OnRecurringRuleModalDataChanged(val recurringRuleModalData: RecurringRuleModalData?) :
        ModifyPlannedSkrinEventi

    data class OnAccountModalDataChanged(val accountModalData: AccountModalData?) :
        ModifyPlannedSkrinEventi
}
