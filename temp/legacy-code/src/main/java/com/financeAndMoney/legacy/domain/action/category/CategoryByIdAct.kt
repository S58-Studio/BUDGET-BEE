package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.category

import com.financeAndMoney.data.database.dao.read.CategoryDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.legacy.datamodel.Category
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import java.util.UUID
import javax.inject.Inject

class CategoryByIdAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<UUID, Category?>() {
    override suspend fun UUID.compose(): suspend () -> Category? = suspend {
        categoryDao.findById(this)?.toLegacyDomain()
    }
}
