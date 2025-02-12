package com.oneSaver.allStatus.domain.data

import androidx.compose.runtime.Immutable
import com.oneSaver.base.legacy.TransactionHistoryItem
import java.time.LocalDate

@Immutable
data class TransactionHistoryDateDivider(
    val date: LocalDate,
    val income: Double,
    val expenses: Double
) : TransactionHistoryItem
