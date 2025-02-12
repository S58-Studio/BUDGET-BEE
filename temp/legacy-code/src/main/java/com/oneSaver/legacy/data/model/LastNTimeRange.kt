package com.oneSaver.legacy.data.model

import androidx.compose.runtime.Immutable
import com.oneSaver.data.model.IntervalType
import com.oneSaver.legacy.forDisplay
import com.oneSaver.legacy.incrementDate
import com.oneSaver.legacy.utils.timeNowUTC
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
