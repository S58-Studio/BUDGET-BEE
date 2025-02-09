package com.financeAndMoney.legacy.data.model

import androidx.compose.runtime.Immutable
import com.financeAndMoney.data.model.IntervalType
import com.financeAndMoney.legacy.forDisplay
import com.financeAndMoney.legacy.incrementDate
import com.financeAndMoney.legacy.utils.timeNowUTC
import java.time.LocalDateTime

@Immutable
data class LastNTimeRange(
    val periodN: Int,
    val periodType: IntervalType,
) {
    fun fromDate(): LocalDateTime = periodType.incrementDate(
        date = timeNowUTC(),
        intervalN = -periodN.toLong()
    )

    fun forDisplay(): String =
        "$periodN ${periodType.forDisplay(periodN)}"
}
