package com.financeAndMoney.domains.usecase.kategori

import arrow.core.Option
import com.financeAndMoney.base.threading.DispatchersProvider
import com.financeAndMoney.data.model.CategoryId
import com.financeAndMoney.data.model.PositiveValue
import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.domains.model.StatisticSummary
import com.financeAndMoney.domains.model.TimeRange
import com.financeAndMoney.domains.usecase.Xchange.ExchangeUseCase
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