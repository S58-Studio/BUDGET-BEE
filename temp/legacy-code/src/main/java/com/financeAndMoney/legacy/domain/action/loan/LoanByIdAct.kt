package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.loan

import com.financeAndMoney.data.database.dao.read.LoanDao
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.legacy.datamodel.Loan
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import java.util.UUID
import javax.inject.Inject

class LoanByIdAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<UUID, Loan?>() {
    override suspend fun UUID.compose(): suspend () -> Loan? = suspend {
        loanDao.findById(this)?.toLegacyDomain()
    }
}
