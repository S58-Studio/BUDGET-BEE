package com.oneSaver.legacy.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.oneSaver.base.TimeProvider
import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.base.time.convertToLocal
import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.data.database.dao.read.SettingsDao
import com.oneSaver.data.model.Tag
import com.oneSaver.data.model.Transaction
import com.oneSaver.data.model.TagId
import com.oneSaver.data.repository.AccountRepository
import com.oneSaver.data.repository.TagRepository
import com.oneSaver.data.repository.mapper.TransactionMapper
import com.oneSaver.frp.Pure
import com.oneSaver.frp.SideEffect
import com.oneSaver.legacy.frp.then
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.temp.toImmutableLegacyTags
import com.oneSaver.legacy.datamodel.temp.toLegacyDomain
import com.oneSaver.legacy.utils.convertUTCtoLocal
import com.oneSaver.legacy.utils.toEpochSeconds
import com.oneSaver.allStatus.domain.data.TransactionHistoryDateDivider
import com.oneSaver.allStatus.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeData
import com.oneSaver.allStatus.domain.pure.exchange.ExchangeTrnArgument
import com.oneSaver.allStatus.domain.pure.exchange.exchangeInBaseCurrency
import com.oneSaver.allStatus.domain.pure.transaction.LegacyFoldTransactions
import com.oneSaver.allStatus.domain.pure.transaction.LegacyTrnFunctions
import com.oneSaver.allStatus.domain.pure.transaction.expenses
import com.oneSaver.allStatus.domain.pure.transaction.incomes
import com.oneSaver.allStatus.domain.pure.transaction.sumTrns
import com.oneSaver.base.time.TimeConverter
import java.math.BigDecimal
import java.util.UUID

@Deprecated("Migrate to actions")
suspend fun List<Transaction>.withDateDividers(
    exchangeRatesLogic: ExchangeRatesLogic,
    settingsDao: SettingsDao,
    accountDao: AccountDao,
    tagRepository: TagRepository,
    accountRepository: AccountRepository,
): List<TransactionHistoryItem> {
    return transactionsWithDateDividers(
        transactions = this,
        baseCurrencyCode = settingsDao.findFirst().currency,
        getAccount = accountDao::findById then { it?.toLegacyDomain() },
        getTags = { tagsIds -> tagRepository.findByIds(tagsIds) },
        accountRepository = accountRepository,
        exchange = { data, amount ->
            exchangeRatesLogic.convertAmount(
                baseCurrency = data.baseCurrency,
                fromCurrency = data.fromCurrency.orNull() ?: "",
                toCurrency = data.toCurrency,
                amount = amount.toDouble()
            ).toBigDecimal().toOption()
        }
    )
}

@Pure
suspend fun transactionsWithDateDividers(
    transactions: List<Transaction>,
    baseCurrencyCode: String,
    accountRepository: AccountRepository,
    @SideEffect
    getAccount: suspend (accountId: UUID) -> Account?,
    @SideEffect
    exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>,
    @SideEffect
    getTags: suspend (tagIds: List<TagId>) -> List<Tag> = { emptyList() },
): List<TransactionHistoryItem> {
    if (transactions.isEmpty()) return emptyList()
    val transactionsMapper = TransactionMapper(accountRepository)
    return transactions
        .groupBy { it.time.convertToLocal().toLocalDate() }
        .filterKeys { it != null }
        .toSortedMap { date1, date2 ->
            if (date1 == null || date2 == null) return@toSortedMap 0 // this case shouldn't happen
            (date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay().toEpochSeconds()).toInt()
        }
        .flatMap { (date, transactionsForDate) ->
            val arg = ExchangeTrnArgument(
                baseCurrency = baseCurrencyCode,
                getAccount = getAccount,
                exchange = exchange
            )

            // Required to be interoperable with [TransactionHistoryItem]
            val legacyTransactionsForDate = with(transactionsMapper) {
                transactionsForDate.map {
                    it.toEntity()
                        .toLegacyDomain(tags = getTags(it.tags).toImmutableLegacyTags())
                }
            }
            listOf<TransactionHistoryItem>(
                TransactionHistoryDateDivider(
                    date = date!!,
                    income = sumTrns(
                        incomes(transactionsForDate),
                        ::exchangeInBaseCurrency,
                        arg
                    ).toDouble(),
                    expenses = sumTrns(
                        expenses(transactionsForDate),
                        ::exchangeInBaseCurrency,
                        arg
                    ).toDouble()
                ),
            ).plus(legacyTransactionsForDate)
        }
}

@Deprecated("Uses legacy Transaction")
object LegacyTrnDateDividers {
    @Deprecated("Migrate to actions")
    suspend fun List<com.oneSaver.base.legacy.Transaction>.withDateDividers(
        exchangeRatesLogic: ExchangeRatesLogic,
        settingsDao: SettingsDao,
        accountDao: AccountDao,
        timeConverter: TimeConverter,
    ): List<TransactionHistoryItem> {
        return transactionsWithDateDividers(
            transactions = this,
            baseCurrencyCode = settingsDao.findFirst().currency,
            getAccount = accountDao::findById then { it?.toLegacyDomain() },
            exchange = { data, amount ->
                exchangeRatesLogic.convertAmount(
                    baseCurrency = data.baseCurrency,
                    fromCurrency = data.fromCurrency.orNull() ?: "",
                    toCurrency = data.toCurrency,
                    amount = amount.toDouble()
                ).toBigDecimal().toOption()
            },
            timeConverter = timeConverter,
        )
    }

    @Pure
    suspend fun transactionsWithDateDividers(
        transactions: List<com.oneSaver.base.legacy.Transaction>,
        baseCurrencyCode: String,
        timeConverter: TimeConverter,

        @SideEffect
        getAccount: suspend (accountId: UUID) -> Account?,
        @SideEffect
        exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>
    ): List<TransactionHistoryItem> {
        if (transactions.isEmpty()) return emptyList()

        return transactions
            .groupBy { with(timeConverter) { it.dateTime?.toLocalDate() } }
            .filterKeys { it != null }
            .toSortedMap { date1, date2 ->
                if (date1 == null || date2 == null) return@toSortedMap 0 // this case shouldn't happen
                (
                        date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay()
                            .toEpochSeconds()
                        ).toInt()
            }
            .flatMap { (date, transactionsForDate) ->
                val arg = ExchangeTrnArgument(
                    baseCurrency = baseCurrencyCode,
                    getAccount = getAccount,
                    exchange = exchange
                )

                listOf<TransactionHistoryItem>(
                    TransactionHistoryDateDivider(
                        date = date!!,
                        income = LegacyFoldTransactions.sumTrns(
                            LegacyTrnFunctions.incomes(transactionsForDate),
                            ::exchangeInBaseCurrency,
                            arg
                        ).toDouble(),
                        expenses = LegacyFoldTransactions.sumTrns(
                            LegacyTrnFunctions.expenses(transactionsForDate),
                            ::exchangeInBaseCurrency,
                            arg
                        ).toDouble()
                    ),
                ).plus(transactionsForDate)
            }
    }
}