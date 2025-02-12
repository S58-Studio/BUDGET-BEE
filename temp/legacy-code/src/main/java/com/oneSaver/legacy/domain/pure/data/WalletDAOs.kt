package com.oneSaver.allStatus.domain.pure.data

import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.data.database.dao.read.ExchangeRatesDao
import com.oneSaver.data.database.dao.read.TransactionDao
import javax.inject.Inject

data class WalletDAOs @Inject constructor(
    val accountDao: AccountDao,
    val transactionDao: TransactionDao,
    val exchangeRatesDao: ExchangeRatesDao
)
