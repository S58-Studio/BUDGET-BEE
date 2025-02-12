package com.oneSaver.data.model.utilities

import com.oneSaver.data.model.PositiveValue
import com.oneSaver.data.model.primitive.PositiveDouble
import java.math.BigDecimal
import java.math.RoundingMode

fun PositiveValue.round(decimalPlaces: Int): PositiveValue = PositiveValue(
    amount = PositiveDouble.unsafe(amount.value.roundTo(decimalPlaces)),
    asset = asset,
)

fun Double.roundTo(decimalPlaces: Int): Double {
    return BigDecimal(this).setScale(decimalPlaces, RoundingMode.HALF_EVEN).toDouble()
}