package com.oneSaver.domains.model

import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.NonNegativeInt
import com.oneSaver.data.model.primitive.PositiveDouble

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