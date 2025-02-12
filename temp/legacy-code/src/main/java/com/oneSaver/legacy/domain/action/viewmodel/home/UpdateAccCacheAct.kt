package com.oneSaver.allStatus.domain.action.viewmodel.home

import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.MySaveCtx
import com.oneSaver.legacy.datamodel.Account
import javax.inject.Inject

class UpdateAccCacheAct @Inject constructor(
    private val MySaveCtx: MySaveCtx
) : FPAction<List<Account>, List<Account>>() {
    override suspend fun List<Account>.compose(): suspend () -> List<Account> = suspend {
        val accounts = this

        MySaveCtx.accountMap.clear()
        MySaveCtx.accountMap.putAll(accounts.map { it.id to it })

        accounts
    }
}
