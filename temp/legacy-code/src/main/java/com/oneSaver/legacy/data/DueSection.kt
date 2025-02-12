package com.oneSaver.legacy.data

import androidx.compose.runtime.Immutable
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import kotlinx.collections.immutable.ImmutableList

@Deprecated("Uses legacy Transaction")
@Immutable
data class LegacyDueSection(
    val trns: ImmutableList<Transaction>,
    val expanded: Boolean,
    val stats: IncomeExpensePair
)

@Deprecated("Legacy data model")
@Immutable
data class DueSection(
    val trns: ImmutableList<com.oneSaver.data.model.Transaction>,
    val expanded: Boolean,
    val stats: IncomeExpensePair
)