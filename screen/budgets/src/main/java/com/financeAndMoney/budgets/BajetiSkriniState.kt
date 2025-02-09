package com.financeAndMoney.budgets

import com.financeAndMoney.budgets.model.DisplayBajeti
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.legacy.data.model.FromToTimeRange
import com.financeAndMoney.legacy.datamodel.Account
import kotlinx.collections.immutable.ImmutableList
import javax.annotation.concurrent.Immutable

@Immutable
data class BajetiSkriniState(
    val baseCurrency: String,
    val budgets: ImmutableList<DisplayBajeti>,
    val categories: ImmutableList<Category>,
    val accounts: ImmutableList<Account>,
    val categoryBudgetsTotal: Double,
    val appBudgetMax: Double,
    val timeRange: FromToTimeRange?,
    val reorderModalVisible: Boolean,
    val budgetModalData: BudgetModalData?
)
