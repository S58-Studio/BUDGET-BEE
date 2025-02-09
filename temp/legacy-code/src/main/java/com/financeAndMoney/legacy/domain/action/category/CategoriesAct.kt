package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.category

import com.financeAndMoney.data.database.dao.read.CategoryDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.action.thenMap
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.Category
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
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
