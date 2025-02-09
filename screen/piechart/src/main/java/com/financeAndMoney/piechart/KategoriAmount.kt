package com.financeAndMoney.piechart

import androidx.compose.runtime.Immutable
import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.data.model.Category

@Immutable
data class KategoriAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false
)
