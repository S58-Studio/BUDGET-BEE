package com.oneSaver.allStatus.domain.pure.data

import com.oneSaver.legacy.utils.beginningOfIvyTime
import com.oneSaver.legacy.utils.timeNowUTC
import java.time.LocalDateTime

data class ClosedTimeRange(
    val from: LocalDateTime,
    val to: LocalDateTime
) {
    companion object {
        fun allTimeIvy(): ClosedTimeRange = ClosedTimeRange(
            from = beginningOfIvyTime(),
            to = timeNowUTC()
        )

        fun to(to: LocalDateTime): ClosedTimeRange = ClosedTimeRange(
            from = beginningOfIvyTime(),
            to = to
        )
    }

    fun toFromToRange(): com.oneSaver.legacy.data.model.FromToTimeRange =
        com.oneSaver.legacy.data.model.FromToTimeRange(
            from = from,
            to = to
        )
}
