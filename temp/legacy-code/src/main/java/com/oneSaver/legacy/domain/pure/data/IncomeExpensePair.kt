package com.oneSaver.allStatus.domain.pure.data

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
data class IncomeExpensePair(
    val income: BigDecimal,
    val expense: BigDecimal
) {
    companion object {
        fun zero(): IncomeExpensePair = IncomeExpensePair(BigDecimal.ZERO, BigDecimal.ZERO)
    }
}
