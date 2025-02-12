package com.oneSaver.accounts

import androidx.compose.runtime.Immutable
import com.oneSaver.legacy.data.model.AccountData
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class ACState(
    val baseCurrency: String,
    val accountsData: ImmutableList<AccountData>,
    val totalBalanceWithExcluded: String,
    val totalBalanceWithExcludedText: String,
    val totalBalanceWithoutExcluded: String,
    val totalBalanceWithoutExcludedText: String,
    val reorderVisible: Boolean
)
