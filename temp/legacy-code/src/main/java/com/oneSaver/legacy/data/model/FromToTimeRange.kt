package com.oneSaver.legacy.data.model

import androidx.compose.runtime.Immutable
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.legacy.utils.beginningOfIvyTime
import com.oneSaver.legacy.utils.dateNowUTC
import com.oneSaver.legacy.utils.formatDateOnly
import com.oneSaver.legacy.utils.startOfDayNowUTC
import com.oneSaver.legacy.utils.timeNowUTC
import com.oneSaver.legacy.utils.toIvyFutureTime
import com.oneSaver.allStatus.domain.pure.data.ClosedTimeRange
import java.time.LocalDateTime
import java.time.ZoneOffset

@Immutable
data class FromToTimeRange(
    val from: LocalDateTime?,
    val to: LocalDateTime?,
) {
    fun from(): LocalDateTime =
        from ?: timeNowUTC().minusYears(30)

    fun to(): LocalDateTime =
        to ?: timeNowUTC().plusYears(30)

    fun upcomingFrom(): LocalDateTime {
        val startOfDayNowUTC =
            startOfDayNowUTC().minusDays(1) // -1 day to ensure that everything is included
        return if (includes(startOfDayNowUTC)) startOfDayNowUTC else from()
    }

    fun overdueTo(): LocalDateTime {
        val startOfDayNowUTC =
            startOfDayNowUTC().plusDays(1) // +1 day to ensure that everything is included
        return if (includes(startOfDayNowUTC)) startOfDayNowUTC else to()
    }

    fun includes(dateTime: LocalDateTime): Boolean =
        dateTime.isAfter(from()) && dateTime.isBefore(to())

    fun toDisplay(): String {
        return when {
            from != null && to != null -> {
                "${from.toLocalDate().formatDateOnly()} - ${to.toLocalDate().formatDateOnly()}"
            }
            from != null && to == null -> {
                "From ${from.toLocalDate().formatDateOnly()}"
            }
            from == null && to != null -> {
                "To ${to.toLocalDate().formatDateOnly()}"
            }
            else -> {
                "Range"
            }
        }
    }
}

@Deprecated("Uses legacy Transaction")
fun Iterable<Transaction>.filterUpcomingLegacy(): List<Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay()

    return filter {
        // make sure that it's in the future
        it.dueDate != null && it.dueDate!!.isAfter(todayStartOfDayUTC)
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
fun Iterable<Transaction>.filterOverdueLegacy(): List<Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay()

    return filter {
        // make sure that it's in the past
        it.dueDate != null && it.dueDate!!.isBefore(todayStartOfDayUTC)
    }
}

fun Iterable<com.oneSaver.data.model.Transaction>.filterOverdue(): List<com.oneSaver.data.model.Transaction> {
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
        from = from ?: beginningOfIvyTime(),
        to = to ?: toIvyFutureTime()
    )
}
