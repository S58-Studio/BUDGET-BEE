package com.oneSaver.allStatus.domain.action.account

import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import java.util.UUID
import javax.inject.Inject

class AccountByIdAct @Inject constructor(
    private val accountDao: AccountDao
) : FPAction<UUID, Account?>() {
    @Deprecated("Legacy code. Don't use it, please.")
    override suspend fun UUID.compose(): suspend () -> Account? = suspend {
        this // accountId
    } then accountDao::findById then {
        it?.toLegacyDomain()
    }
}
