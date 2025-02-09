package com.financeAndMoney.piechart

import androidx.compose.runtime.Immutable
import com.financeAndMoney.data.model.Category

@Immutable
data class SelectedKategori(
    val category: Category // null - Unspecified
)
