package com.oneSaver.legacy.data

import androidx.compose.runtime.Immutable
import com.oneSaver.data.model.Category
import com.oneSaver.legacy.datamodel.Account
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class AppBaseData(
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)
