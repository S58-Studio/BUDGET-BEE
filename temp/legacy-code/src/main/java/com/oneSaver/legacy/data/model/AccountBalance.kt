package com.oneSaver.legacy.data.model

import androidx.compose.runtime.Immutable
import com.oneSaver.legacy.datamodel.Account

@Immutable
data class AccountBalance(
    val account: Account,
    val balance: Double
)
