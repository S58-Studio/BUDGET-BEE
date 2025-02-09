package com.financeAndMoney.legacy.ui.component.transaction

import androidx.compose.runtime.Composable
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.legacy.mySaveCtx
import java.util.UUID

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun category(
    categoryId: UUID?,
    categories: List<Category>
): Category? {
    val targetId = categoryId ?: return null
    return mySaveCtx().categoryMap[targetId] ?: categories.find { it.id.value == targetId }
}

@Deprecated("Old design system. Use `:financeAndMoney-design` and Material3")
@Composable
fun account(
    accountId: UUID?,
    accounts: List<Account>
): Account? {
    val targetId = accountId ?: return null
    return mySaveCtx().accountMap[targetId] ?: accounts.find { it.id == targetId }
}
