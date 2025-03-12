package com.oneSaver.allStatus.domain.action.transaction

import com.oneSaver.base.TimeProvider
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.data.repository.AccountRepository
import com.oneSaver.data.repository.TagRepository
import com.oneSaver.frp.action.FPAction
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.legacy.domain.pure.transaction.LegacyTrnDateDividers
import com.oneSaver.legacy.domain.pure.transaction.transactionsWithDateDividers
import com.oneSaver.allStatus.domain.action.exchange.ExchangeAct
import com.oneSaver.allStatus.domain.action.exchange.actInput
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
        val transactions: List<com.oneSaver.data.model.Transaction>
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