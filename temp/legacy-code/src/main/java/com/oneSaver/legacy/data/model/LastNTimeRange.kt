package com.oneSaver.legacy.data.model

import androidx.compose.runtime.Immutable
import com.oneSaver.base.time.TimeProvider
import com.oneSaver.data.model.IntervalType
import com.oneSaver.legacy.forDisplay
import com.oneSaver.legacy.incrementDate
import com.oneSaver.legacy.utils.timeNowUTC
import java.time.Instant
import java.time.LocalDateTime

@Suppress("DataClassFunctions")
@Immutable
data class LastNTimeRange(
    val periodN: Int,
    val periodType: IntervalType,
) {
    fun fromDate(
        timeProvider: TimeProvider
    ): Instant = periodType.incrementDate(
        date = timeProvider.utcNow(),
        intervalN = -periodN.toLong()
    )

    fun forDisplay(): String =
        "$periodN ${periodType.forDisplay(periodN)}"
}
