package com.oneSaver.piechart

import androidx.compose.runtime.Immutable
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.data.model.Category

@Immutable
data class KategoriAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false
)
