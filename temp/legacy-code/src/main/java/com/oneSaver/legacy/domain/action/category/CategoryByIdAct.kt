package com.oneSaver.allStatus.domain.action.category

import com.oneSaver.data.database.dao.read.CategoryDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.datamodel.Category
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import java.util.UUID
import javax.inject.Inject

class CategoryByIdAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<UUID, Category?>() {
    override suspend fun UUID.compose(): suspend () -> Category? = suspend {
        categoryDao.findById(this)?.toLegacyDomain()
    }
}
