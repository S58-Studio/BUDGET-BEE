package com.oneSaver.allStatus.domain.action.category

import com.oneSaver.data.database.dao.read.CategoryDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.action.thenMap
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.Category
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class CategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<Unit, ImmutableList<Category>>() {
    override suspend fun Unit.compose(): suspend () -> ImmutableList<Category> = suspend {
        io {
            categoryDao.findAll()
        }
    } thenMap { it.toLegacyDomain() } then { it.toImmutableList() }
}
