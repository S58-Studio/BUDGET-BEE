package com.oneSaver.domains.usecase.myLonWallet

import arrow.core.Option
import com.oneSaver.data.model.PositiveValue
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.domains.model.StatisticSummary
import com.oneSaver.domains.model.TimeRange
import com.oneSaver.domains.usecase.exchange.ExchangeUseCase
import javax.inject.Inject

@Suppress("UnusedPrivateProperty", "UnusedParameter")
class WalletStatsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val exchangeUseCase: ExchangeUseCase,
) {
    /**
     * Calculates the stats for MySave App including excluded accounts.
     * It ignores transfers and focuses only on income and expenses.
     * Stats that can't be exchanged in [outCurrency] are skipped
     * and accumulated as [ExchangedWalletStats.exchangeErrors].
     */
    suspend fun calculate(
        range: TimeRange,
        outCurrency: AssetCode
    ): ExchangedWalletStats {
        // Use the StatSummaryBuilder
        TODO("Not implemented")
    }

    /**
     * Calculates the stats for MySave App including excluded accounts.
     * It ignores transfers and focuses only on income and expenses.
     */
    suspend fun calculate(
        timeRange: TimeRange
    ): WalletStats {
        // Use the StatSummaryBuilder
        TODO("Not implemented")
    }
}

data class WalletStats(
    val income: StatisticSummary,
    val expense: StatisticSummary,
)

data class ExchangedWalletStats(
    val income: Option<PositiveValue>,
    val expense: Option<PositiveValue>,
    val exchangeErrors: Set<AssetCode>,
)