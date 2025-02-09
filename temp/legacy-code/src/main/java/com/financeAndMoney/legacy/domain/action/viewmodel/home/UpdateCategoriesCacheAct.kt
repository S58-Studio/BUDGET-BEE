package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.viewmodel.home

import com.financeAndMoney.data.model.Category
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.legacy.MySaveCtx
import javax.inject.Inject

class UpdateCategoriesCacheAct @Inject constructor(
    private val MySaveCtx: MySaveCtx
) : FPAction<List<Category>, List<Category>>() {
    override suspend fun List<Category>.compose(): suspend () -> List<Category> = suspend {
        val categories = this

        MySaveCtx.categoryMap.clear()
        MySaveCtx.categoryMap.putAll(categories.map { it.id.value to it })

        categories
    }
}
