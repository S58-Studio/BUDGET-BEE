package com.oneSaver.allStatus.domain.action.category

import com.oneSaver.base.legacy.Transaction
import com.oneSaver.data.database.dao.read.TransactionDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.action.thenMap
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.legacy.domain.pure.data.ClosedTimeRange
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
