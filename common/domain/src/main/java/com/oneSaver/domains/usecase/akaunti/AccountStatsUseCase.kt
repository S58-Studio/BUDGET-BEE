package com.oneSaver.domains.usecase.akaunti

import arrow.core.Option
import com.oneSaver.base.threading.DispatchersProvider
import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.Expense
import com.oneSaver.data.model.Income
import com.oneSaver.data.model.PositiveValue
import com.oneSaver.data.model.Transaction
import com.oneSaver.data.model.Transfer
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.repository.AccountRepository
import com.oneSaver.domains.model.StatisticSummary
import com.oneSaver.domains.model.TimeRange
import com.oneSaver.domains.usecase.StatSummaryBuilder
import com.oneSaver.domains.usecase.exchange.ExchangeUseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("UnusedPrivateProperty", "UnusedParameter")
class AccountStatsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val accountRepository: AccountRepository,
    private val exchangeUseCase: ExchangeUseCase,
) {

    suspend fun calculate(
        account: AccountId,
        range: TimeRange,
        outCurrency: AssetCode
    ): ExchangedAccountStats {
        TODO("Not implemented")
    }

    suspend fun calculate(
        account: AccountId,
        range: TimeRange,
        transactions: List<Transaction>,
    ): ExchangedAccountStats {
        TODO("Not implemented")
    }

    suspend fun calculate(
        account: AccountId,
        range: TimeRange
    ): AccountStats {
        TODO("Not implemented")
    }

    suspend fun calculate(
        account: AccountId,
        transactions: List<Transaction>
    ): AccountStats = withContext(dispatchers.default) {
        val income = StatSummaryBuilder()
        val expense = StatSummaryBuilder()
        val transfersIn = StatSummaryBuilder()
        val transfersOut = StatSummaryBuilder()

        for (trn in transactions) {
            when (trn) {
                is Expense -> if (trn.account == account) {
                    expense.process(trn.value)
                }

                is Income -> if (trn.account == account) {
                    income.process(trn.value)
                }

                is Transfer -> {
                    when (account) {
                        trn.fromAccount -> transfersOut.process(trn.fromValue)
                        trn.toAccount -> transfersIn.process(trn.toValue)
                        else -> {
                            // ignore, not relevant transfer for the account
                        }
                    }
                }
            }
        }

        AccountStats(
            income = income.build(),
            expense = expense.build(),
            transfersIn = transfersIn.build(),
            transfersOut = transfersOut.build()
        )
    }
}

data class AccountStats(
    val income: StatisticSummary,
    val expense: StatisticSummary,
    val transfersIn: StatisticSummary,
    val transfersOut: StatisticSummary,
) {
    companion object {
        val Zero = AccountStats(
            income = StatisticSummary.Zero,
            expense = StatisticSummary.Zero,
            transfersIn = StatisticSummary.Zero,
            transfersOut = StatisticSummary.Zero,
        )
    }
}

data class ExchangedAccountStats(
    val income: Option<PositiveValue>,
    val expense: Option<PositiveValue>,
    val transfersIn: Option<PositiveValue>,
    val transfersOut: Option<PositiveValue>,
    val exchangeErrors: Set<AssetCode>,
)