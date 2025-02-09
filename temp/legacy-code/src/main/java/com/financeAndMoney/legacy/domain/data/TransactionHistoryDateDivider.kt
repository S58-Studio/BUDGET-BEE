package com.financeAndMoney.expenseAndBudgetPlanner.domain.data

import androidx.compose.runtime.Immutable
import com.financeAndMoney.base.legacy.TransactionHistoryItem
import java.time.LocalDate

@Immutable
data class TransactionHistoryDateDivider(
    val date: LocalDate,
    val income: Double,
    val expenses: Double
) : TransactionHistoryItem
