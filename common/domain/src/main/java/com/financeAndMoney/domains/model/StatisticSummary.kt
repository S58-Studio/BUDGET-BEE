package com.financeAndMoney.domains.model

import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.model.primitive.NonNegativeInt
import com.financeAndMoney.data.model.primitive.PositiveDouble

data class StatisticSummary(
    val trnCount: NonNegativeInt,
    val values: Map<AssetCode, PositiveDouble>,
) {
    companion object {
        val Zero = StatisticSummary(
            values = emptyMap(),
            trnCount = NonNegativeInt.Zero
        )
    }
}