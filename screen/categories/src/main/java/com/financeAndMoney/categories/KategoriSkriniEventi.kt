package com.financeAndMoney.categories

import com.financeAndMoney.expenseAndBudgetPlanner.domain.data.SortOrder
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateCategoryData
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.edit.CategoryModalData

sealed interface KategoriSkriniEventi {
    data class OnReorder(
        val newOrder: List<KategoriData>,
        val sortOrder: SortOrder = SortOrder.DEFAULT
    ) : KategoriSkriniEventi

    data class OnCreateCategory(val createCategoryData: CreateCategoryData) :
        KategoriSkriniEventi

    data class OnReorderModalVisible(val visible: Boolean) : KategoriSkriniEventi
    data class OnSortOrderModalVisible(val visible: Boolean) : KategoriSkriniEventi
    data class OnCategoryModalVisible(val categoryModalData: CategoryModalData?) :
        KategoriSkriniEventi
}
