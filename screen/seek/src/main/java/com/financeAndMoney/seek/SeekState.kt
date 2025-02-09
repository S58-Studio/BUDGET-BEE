package com.financeAndMoney.seek

import com.financeAndMoney.base.legacy.TransactionHistoryItem
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.legacy.datamodel.Account
import kotlinx.collections.immutable.ImmutableList

data class SeekState(
    val searchQuery: String,
    val transactions: ImmutableList<TransactionHistoryItem>,
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)
