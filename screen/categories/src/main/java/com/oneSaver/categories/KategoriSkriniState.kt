package com.oneSaver.categories

import com.oneSaver.allStatus.domain.data.SortOrder
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModalData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class KategoriSkriniState(
    val baseCurrency: String = "",
    val categories: ImmutableList<KategoriData> = persistentListOf(),
    val reorderModalVisible: Boolean = false,
    val categoryModalData: CategoryModalData? = null,
    val sortModalVisible: Boolean = false,
    val sortOrderItems: ImmutableList<SortOrder> = SortOrder.values().toList().toImmutableList(),
    val sortOrder: SortOrder = SortOrder.DEFAULT
)
