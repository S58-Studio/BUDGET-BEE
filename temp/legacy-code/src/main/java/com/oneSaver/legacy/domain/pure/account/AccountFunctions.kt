package com.oneSaver.allStatus.domain.pure.account

import com.oneSaver.legacy.datamodel.Account

fun filterExcluded(accounts: List<Account>): List<Account> =
    accounts.filter { it.includeInBalance }

fun accountCurrency(account: Account, baseCurrency: String): String =
    account.currency ?: baseCurrency
