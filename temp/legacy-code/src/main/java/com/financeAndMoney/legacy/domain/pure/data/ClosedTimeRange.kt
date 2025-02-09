package com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data

import com.financeAndMoney.legacy.utils.beginningOfIvyTime
import com.financeAndMoney.legacy.utils.timeNowUTC
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

    fun toFromToRange(): com.financeAndMoney.legacy.data.model.FromToTimeRange =
        com.financeAndMoney.legacy.data.model.FromToTimeRange(
            from = from,
            to = to
        )
}
