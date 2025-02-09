package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.transaction

import com.financeAndMoney.base.TimeProvider
import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.base.legacy.TransactionHistoryItem
import com.financeAndMoney.data.database.dao.read.AccountDao
import com.financeAndMoney.data.repository.AccountRepository
import com.financeAndMoney.data.repository.TagRepository
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import com.financeAndMoney.legacy.domain.pure.transaction.LegacyTrnDateDividers
import com.financeAndMoney.legacy.domain.pure.transaction.transactionsWithDateDividers
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.exchange.ExchangeAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.exchange.actInput
import javax.inject.Inject

class TrnsWithDateDivsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val exchangeAct: ExchangeAct,
    private val tagRepository: TagRepository,
    private val accountRepository: AccountRepository,
    private val timeProvider: TimeProvider,
) : FPAction<TrnsWithDateDivsAct.Input, List<TransactionHistoryItem>>() {

    override suspend fun Input.compose(): suspend () -> List<TransactionHistoryItem> = suspend {
        transactionsWithDateDividers(
            transactions = transactions,
            baseCurrencyCode = baseCurrency,
            getTags = { tagIds -> tagRepository.findByIds(tagIds) },
            getAccount = accountDao::findById then { it?.toLegacyDomain() },
            accountRepository = accountRepository,
            timeProvider = timeProvider,
            exchange = ::actInput then exchangeAct
        )
    }

    data class Input(
        val baseCurrency: String,
        val transactions: List<com.financeAndMoney.data.model.Transaction>
    )
}

@Deprecated("Uses legacy Transaction")
class LegacyTrnsWithDateDivsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val exchangeAct: ExchangeAct
) : FPAction<LegacyTrnsWithDateDivsAct.Input, List<TransactionHistoryItem>>() {

    override suspend fun Input.compose(): suspend () -> List<TransactionHistoryItem> = suspend {
        LegacyTrnDateDividers.transactionsWithDateDividers(
            transactions = transactions,
            baseCurrencyCode = baseCurrency,

            getAccount = accountDao::findById then { it?.toLegacyDomain() },
            exchange = ::actInput then exchangeAct
        )
    }

    data class Input(
        val baseCurrency: String,
        val transactions: List<Transaction>
    )
}