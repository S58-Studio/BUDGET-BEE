package com.oneSaver.legacy.data.model

import androidx.compose.runtime.Immutable
import com.oneSaver.data.model.Transaction
import com.oneSaver.legacy.utils.dateNowUTC
import com.oneSaver.legacy.domain.pure.data.ClosedTimeRange
import com.oneSaver.base.time.TimeConverter
import com.oneSaver.base.time.TimeProvider
import com.oneSaver.legacy.utils.ivyMaxTime
import com.oneSaver.legacy.utils.ivyMinTime
import com.oneSaver.userInterface.time.TimeFormatter
import java.time.Instant
import java.time.ZoneOffset

@Suppress("DataClassFunctions")
@Immutable
data class FromToTimeRange(
    val from: Instant?,
    val to: Instant?,
) {
    fun from(): Instant =
        from ?: ivyMinTime()

    fun to(): Instant =
        to ?: ivyMaxTime()

    fun upcomingFrom(
        timeProvider: TimeProvider
    ): Instant {
        val startOfDayNowUTC = timeProvider.utcNow()
        return if (includes(startOfDayNowUTC)) startOfDayNowUTC else from()
    }

    fun overdueTo(
        timeProvider: TimeProvider
    ): Instant {
        val startOfDayNowUTC = timeProvider.utcNow()
        return if (includes(startOfDayNowUTC)) startOfDayNowUTC else to()
    }

    fun includes(dateTime: Instant): Boolean =
        dateTime.isAfter(from()) && dateTime.isBefore(to())

    fun toDisplay(
        timeFormatter: TimeFormatter
    ): String = with(timeFormatter) {
        val style = TimeFormatter.Style.DateOnly(includeWeekDay = false)
        when {
            from != null && to != null -> {
                "${from.formatLocal(style)} - ${to.formatLocal(style)}"
            }

            from != null && to == null -> {
                "From ${from.formatLocal(style)}"
            }

            from == null && to != null -> {
                "To ${to.formatLocal(style)}"
            }

            else -> {
                "Range"
            }
        }
    }
}

@Deprecated("Uses legacy Transaction")
fun Iterable<com.oneSaver.base.legacy.Transaction>.filterUpcomingLegacy(
    timeProvider: TimeProvider,
    timeConverter: TimeConverter,
): List<com.oneSaver.base.legacy.Transaction> {
    val todayStartOfDayUtc = todayStartOfDayUtc(timeProvider, timeConverter)
    return filter {
        // make sure that it's in the future
        it.dueDate != null && it.dueDate!!.isAfter(todayStartOfDayUtc)
    }
}

fun Iterable<com.oneSaver.data.model.Transaction>.filterUpcoming(): List<com.oneSaver.data.model.Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay().toInstant(ZoneOffset.UTC)

    return filter {
        // make sure that it's in the future
        !it.settled && it.time.isAfter(todayStartOfDayUTC)
    }
}

@Deprecated("Uses legacy Transaction")
fun Iterable<com.oneSaver.base.legacy.Transaction>.filterOverdueLegacy(
    timeProvider: TimeProvider,
    timeConverter: TimeConverter,
): List<com.oneSaver.base.legacy.Transaction> {
    val todayStartOfDayUTC = todayStartOfDayUtc(timeProvider, timeConverter)
    return filter {
        // make sure that it's in the past
        it.dueDate != null && it.dueDate!!.isBefore(todayStartOfDayUTC)
    }
}

fun todayStartOfDayUtc(
    timeProvider: TimeProvider,
    timeConverter: TimeConverter,
): Instant = with(timeConverter) {
    timeProvider.localNow()
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .toUTC()
}

fun Iterable<Transaction>.filterOverdue(): List<Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay().toInstant(ZoneOffset.UTC)

    return filter {
        // make sure that it's in the past
        !it.settled && it.time.isBefore(todayStartOfDayUTC)
    }
}

fun FromToTimeRange.toCloseTimeRangeUnsafe(): ClosedTimeRange {
    return ClosedTimeRange(
        from = from(),
        to = to()
    )
}

fun FromToTimeRange.toCloseTimeRange(): ClosedTimeRange {
    return ClosedTimeRange(
        from = from ?: ivyMinTime(),
        to = to ?: ivyMaxTime()
    )
}

fun FromToTimeRange.toUTCCloseTimeRange(): ClosedTimeRange {
    return ClosedTimeRange(
        from = from ?: ivyMinTime(),
        to = to ?: ivyMaxTime()
    )
}