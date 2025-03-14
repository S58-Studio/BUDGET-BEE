package com.oneSaver.base.threading

import kotlin.coroutines.CoroutineContext

interface DispatchersProvider {
    val main: CoroutineContext
    val io: CoroutineContext
    val default: CoroutineContext
}