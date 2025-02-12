package com.oneSaver.budgets

import com.oneSaver.base.legacy.stringRes
import com.oneSaver.core.userInterface.R

fun determineBudgetType(categoriesCount: Int): String {
    return when (categoriesCount) {
        0 -> stringRes(R.string.total_budget)
        1 -> stringRes(R.string.category_budget)
        else -> stringRes(R.string.multi_category_budget, categoriesCount.toString())
    }
}
