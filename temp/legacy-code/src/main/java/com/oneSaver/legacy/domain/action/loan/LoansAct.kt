package com.oneSaver.allStatus.domain.action.loan

import com.oneSaver.data.database.dao.read.LoanDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.frp.action.thenMap
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.Loan
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class LoansAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<Unit, ImmutableList<Loan>>() {
    override suspend fun Unit.compose(): suspend () -> ImmutableList<Loan> = suspend {
        loanDao.findAll()
    } thenMap { it.toLegacyDomain() } then { it.toImmutableList() }
}
