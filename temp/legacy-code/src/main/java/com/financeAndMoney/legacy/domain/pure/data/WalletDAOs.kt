package com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data

import com.financeAndMoney.data.database.dao.read.AccountDao
import com.financeAndMoney.data.database.dao.read.ExchangeRatesDao
import com.financeAndMoney.data.database.dao.read.TransactionDao
import javax.inject.Inject

data class WalletDAOs @Inject constructor(
    val accountDao: AccountDao,
    val transactionDao: TransactionDao,
    val exchangeRatesDao: ExchangeRatesDao
)
