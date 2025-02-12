package com.oneSaver.allStatus.domain.action.account

import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class AccountsAct @Inject constructor(
    private val accountDao: AccountDao
) : FPAction<Unit, ImmutableList<Account>>() {

    override suspend fun Unit.compose(): suspend () -> ImmutableList<Account> = suspend {
        io { accountDao.findAll().map { it.toLegacyDomain() }.toImmutableList() }
    }
}
