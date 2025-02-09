package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.category

import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.data.database.dao.read.TransactionDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.action.thenMap
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.ClosedTimeRange
import java.util.UUID
import javax.inject.Inject

class CategoryTrnsBetweenAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<CategoryTrnsBetweenAct.Input, List<Transaction>>() {

    override suspend fun Input.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionDao.findAllByCategoryAndBetween(
                startDate = between.from,
                endDate = between.to,
                categoryId = categoryId
            )
        }
    } thenMap { it.toLegacyDomain() }

    data class Input(
        val categoryId: UUID,
        val between: ClosedTimeRange
    )
}

fun actInput(
    categoryId: UUID,
    between: ClosedTimeRange
) = CategoryTrnsBetweenAct.Input(
    categoryId = categoryId,
    between = between
)
