package com.oneSaver.domains.usecase

import com.oneSaver.data.model.PositiveValue
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.NonNegativeInt
import com.oneSaver.data.model.primitive.PositiveDouble
import com.oneSaver.domains.model.StatisticSummary

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