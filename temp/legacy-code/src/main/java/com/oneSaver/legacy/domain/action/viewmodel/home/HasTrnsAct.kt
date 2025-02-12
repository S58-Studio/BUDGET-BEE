package com.oneSaver.allStatus.domain.action.viewmodel.home

import com.oneSaver.data.database.dao.read.TransactionDao
import com.oneSaver.frp.action.FPAction
import javax.inject.Inject

class HasTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<Unit, Boolean>() {
    override suspend fun Unit.compose(): suspend () -> Boolean = suspend {
        io {
            transactionDao.findAll_LIMIT_1().isNotEmpty()
        }
    }
}
