package com.financeAndMoney.base.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun Instant.convertToLocal(): ZonedDateTime {
    return atZone(ZoneId.systemDefault())
}
