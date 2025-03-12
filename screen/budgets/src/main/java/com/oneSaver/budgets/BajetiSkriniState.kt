package com.oneSaver.budgets

import com.oneSaver.budgets.model.DisplayBajeti
import com.oneSaver.data.model.Category
import com.oneSaver.legacy.data.model.FromToTimeRange
import com.oneSaver.legacy.datamodel.Account
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
    val totalRemainingBudgetText: String?,
    val timeRange: FromToTimeRange?,
    val reorderModalVisible: Boolean,
    val budgetModalData: BudgetModalData?
)
