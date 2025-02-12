package com.oneSaver.seek

import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.data.model.Category
import com.oneSaver.legacy.datamodel.Account
import kotlinx.collections.immutable.ImmutableList

data class SeekState(
    val searchQuery: String,
    val transactions: ImmutableList<TransactionHistoryItem>,
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)
