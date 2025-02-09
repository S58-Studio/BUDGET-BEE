package com.financeAndMoney.domains.usecase

import com.financeAndMoney.data.model.PositiveValue
import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.model.primitive.NonNegativeInt
import com.financeAndMoney.data.model.primitive.PositiveDouble
import com.financeAndMoney.domains.model.StatisticSummary

class StatSummaryBuilder {
    private var count = 0
    private val values = mutableMapOf<AssetCode, PositiveDouble>()

    fun process(value: PositiveValue) {
        count++
        val asset = value.asset
        // Because it might overflow
        PositiveDouble.from(
            (values[asset]?.value ?: 0.0) + value.amount.value
        ).onRight { newValue ->
            values[asset] = newValue
        }
    }

    fun build(): StatisticSummary = StatisticSummary(
        trnCount = NonNegativeInt.unsafe(count),
        values = values,
    )
}