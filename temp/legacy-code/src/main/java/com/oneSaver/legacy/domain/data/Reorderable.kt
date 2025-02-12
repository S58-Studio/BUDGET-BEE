package com.oneSaver.allStatus.domain.data

interface Reorderable {
    fun getItemOrderNum(): Double

    fun withNewOrderNum(newOrderNum: Double): Reorderable
}
