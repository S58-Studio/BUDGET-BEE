package com.oneSaver.piechart

import androidx.compose.runtime.Immutable
import com.oneSaver.data.model.Category

@Immutable
data class SelectedKategori(
    val category: Category // null - Unspecified
)
