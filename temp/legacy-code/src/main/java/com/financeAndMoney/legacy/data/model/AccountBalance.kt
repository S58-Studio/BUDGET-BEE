package com.financeAndMoney.legacy.data.model

import androidx.compose.runtime.Immutable
import com.financeAndMoney.legacy.datamodel.Account

@Immutable
data class AccountBalance(
    val account: Account,
    val balance: Double
)
