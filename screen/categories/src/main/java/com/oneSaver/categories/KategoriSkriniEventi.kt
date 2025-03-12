package com.oneSaver.categories

import com.oneSaver.allStatus.domain.data.SortOrder
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModalData

sealed interface KategoriSkriniEventi {
    data class OnReorder(
        val newOrder: List<KategoriData>,
        val sortOrder: SortOrder = SortOrder.DEFAULT
    ) : KategoriSkriniEventi

    data class OnCreateCategory(val createCategoryData: CreateCategoryData) :
        KategoriSkriniEventi

    data class OnReorderModalVisible(val visible: Boolean) : KategoriSkriniEventi
    data class OnSortOrderModalVisible(val visible: Boolean) : KategoriSkriniEventi
    data class OnCategoryModalVisible(val categoryModalData: CategoryModalData?) :
        KategoriSkriniEventi
    data class OnSearchQueryUpdate(val queryString: String) : KategoriSkriniEventi
}
