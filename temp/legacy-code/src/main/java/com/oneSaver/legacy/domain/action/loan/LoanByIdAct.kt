package com.oneSaver.allStatus.domain.action.loan

import com.oneSaver.data.database.dao.read.LoanDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.datamodel.Loan
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import java.util.UUID
import javax.inject.Inject

class LoanByIdAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<UUID, Loan?>() {
    override suspend fun UUID.compose(): suspend () -> Loan? = suspend {
        loanDao.findById(this)?.toLegacyDomain()
    }
}
