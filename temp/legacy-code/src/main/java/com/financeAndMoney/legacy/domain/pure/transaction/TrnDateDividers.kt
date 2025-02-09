package com.financeAndMoney.legacy.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.financeAndMoney.base.TimeProvider
import com.financeAndMoney.base.legacy.TransactionHistoryItem
import com.financeAndMoney.base.time.convertToLocal
import com.financeAndMoney.data.database.dao.read.AccountDao
import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.data.model.Tag
import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.model.TagId
import com.financeAndMoney.data.repository.AccountRepository
import com.financeAndMoney.data.repository.TagRepository
import com.financeAndMoney.data.repository.mapper.TransactionMapper
import com.financeAndMoney.frp.Pure
import com.financeAndMoney.frp.SideEffect
import com.financeAndMoney.frp.then
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.datamodel.temp.toImmutableLegacyTags
import com.financeAndMoney.legacy.datamodel.temp.toLegacyDomain
import com.financeAndMoney.legacy.utils.convertUTCtoLocal
import com.financeAndMoney.legacy.utils.toEpochSeconds
import com.financeAndMoney.expenseAndBudgetPlanner.domain.data.TransactionHistoryDateDivider
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.ExchangeData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.ExchangeTrnArgument
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.exchangeInBaseCurrency
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.LegacyFoldTransactions
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.LegacyTrnFunctions
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.expenses
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.incomes
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction.sumTrns
import java.math.BigDecimal
import java.util.UUID

@Deprecated("Migrate to actions")
suspend fun List<Transaction>.withDateDividers(
    exchangeRatesLogic: ExchangeRatesLogic,
    settingsDao: SettingsDao,
    accountDao: AccountDao,
    tagRepository: TagRepository,
    accountRepository: AccountRepository,
    timeProvider: TimeProvider,
): List<TransactionHistoryItem> {
    return transactionsWithDateDividers(
        transactions = this,
        baseCurrencyCode = settingsDao.findFirst().currency,
        getAccount = accountDao::findById then { it?.toLegacyDomain() },
        getTags = { tagsIds -> tagRepository.findByIds(tagsIds) },
        accountRepository = accountRepository,
        timeProvider = timeProvider,
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
    timeProvider: TimeProvider,

    @SideEffect
    getAccount: suspend (accountId: UUID) -> Account?,
    @SideEffect
    exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>,
    @SideEffect
    getTags: suspend (tagIds: List<TagId>) -> List<Tag> = { emptyList() },
): List<TransactionHistoryItem> {
    if (transactions.isEmpty()) return emptyList()
    val transactionsMapper = TransactionMapper(accountRepository, timeProvider)
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
    suspend fun List<com.financeAndMoney.base.legacy.Transaction>.withDateDividers(
        exchangeRatesLogic: ExchangeRatesLogic,
        settingsDao: SettingsDao,
        accountDao: AccountDao
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
            }
        )
    }

    @Pure
    suspend fun transactionsWithDateDividers(
        transactions: List<com.financeAndMoney.base.legacy.Transaction>,
        baseCurrencyCode: String,

        @SideEffect
        getAccount: suspend (accountId: UUID) -> Account?,
        @SideEffect
        exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>
    ): List<TransactionHistoryItem> {
        if (transactions.isEmpty()) return emptyList()

        return transactions
            .groupBy { it.dateTime?.convertUTCtoLocal()?.toLocalDate() }
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