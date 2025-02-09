package com.financeAndMoney.legacy.data

import androidx.compose.runtime.Immutable
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.legacy.datamodel.Account
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class AppBaseData(
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)
