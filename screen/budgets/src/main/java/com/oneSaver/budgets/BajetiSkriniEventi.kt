package com.oneSaver.budgets

import com.oneSaver.budgets.model.DisplayBajeti
import com.oneSaver.legacy.datamodel.Budget
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateBudgetData

sealed interface BajetiSkriniEventi {
    data class OnReorder(val newOrder: List<DisplayBajeti>) : BajetiSkriniEventi
    data class OnCreateBudget(val budgetData: CreateBudgetData) : BajetiSkriniEventi
    data class OnEditBudget(val budget: Budget) : BajetiSkriniEventi
    data class OnDeleteBudget(val budget: Budget) : BajetiSkriniEventi
    data class OnReorderModalVisible(val visible: Boolean) : BajetiSkriniEventi
    data class OnBudgetModalData(val budgetModalData: BudgetModalData?) : BajetiSkriniEventi
}
