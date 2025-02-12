package com.oneSaver.allStatus.domain.action.viewmodel.home

import com.oneSaver.data.model.Category
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.MySaveCtx
import javax.inject.Inject

class UpdateCategoriesCacheAct @Inject constructor(
    private val MySaveCtx: MySaveCtx
) : FPAction<List<Category>, List<Category>>() {
    override suspend fun List<Category>.compose(): suspend () -> List<Category> = suspend {
        val categories = this

        MySaveCtx.categoryMap.clear()
        MySaveCtx.categoryMap.putAll(categories.map { it.id.value to it })

        categories
    }
}
