package com.financeAndMoney.budgets

import com.financeAndMoney.budgets.model.DisplayBajeti
import com.financeAndMoney.legacy.datamodel.Budget
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateBudgetData

sealed interface BajetiSkriniEventi {
    data class OnReorder(val newOrder: List<DisplayBajeti>) : BajetiSkriniEventi
    data class OnCreateBudget(val budgetData: CreateBudgetData) : BajetiSkriniEventi
    data class OnEditBudget(val budget: Budget) : BajetiSkriniEventi
    data class OnDeleteBudget(val budget: Budget) : BajetiSkriniEventi
    data class OnReorderModalVisible(val visible: Boolean) : BajetiSkriniEventi
    data class OnBudgetModalData(val budgetModalData: BudgetModalData?) : BajetiSkriniEventi
}
