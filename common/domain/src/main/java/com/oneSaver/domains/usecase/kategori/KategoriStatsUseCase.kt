package com.oneSaver.domains.usecase.kategori

import arrow.core.Option
import com.oneSaver.base.threading.DispatchersProvider
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.PositiveValue
import com.oneSaver.data.model.Transaction
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.domains.model.StatisticSummary
import com.oneSaver.domains.model.TimeRange
import com.oneSaver.domains.usecase.exchange.ExchangeUseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("UnusedPrivateProperty", "UnusedParameter")
class CategoryStatsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val exchangeUseCase: ExchangeUseCase,
) {
    suspend fun calculate(
        category: CategoryId,
        range: TimeRange,
        outCurrency: AssetCode,
    ): ExchangedCategoryStats {
        TODO("Not implemented")
    }

    suspend fun calculate(
        category: CategoryId,
        range: TimeRange,
    ): CategoryStats {
        TODO("Not implemented")
    }

    suspend fun calculate(
        category: CategoryId,
        transactions: List<Transaction>,
        outCurrency: AssetCode,
    ): ExchangedCategoryStats {
        TODO("Not implemented")
    }

    suspend fun calculate(
        category: CategoryId,
        transactions: List<Transaction>
    ): CategoryStats = withContext(dispatchers.default) {
        TODO("Not implemented")
    }
}

data class CategoryStats(
    val income: StatisticSummary,
    val expense: StatisticSummary,
) {
    companion object {
        val Zero = CategoryStats(
            income = StatisticSummary.Zero,
            expense = StatisticSummary.Zero,
        )
    }
}

data class ExchangedCategoryStats(
    val income: Option<PositiveValue>,
    val expense: Option<PositiveValue>,
    val exchangeErrors: Set<AssetCode>
)