package com.oneSaver.base

import java.time.ZoneId
import javax.inject.Inject

@Suppress("UnnecessaryPassThroughClass")
class TimeProvider @Inject constructor() {
    fun getZoneId(): ZoneId = ZoneId.systemDefault()
}