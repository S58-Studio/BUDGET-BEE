package com.financeAndMoney.onboarding

import androidx.compose.runtime.Immutable
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.legacy.data.model.AccountBalance
import com.financeAndMoney.legacy.domain.data.MysaveCurrency
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateAccountData
import com.financeAndMoney.expenseAndBudgetPlanner.domain.deprecated.logic.model.CreateCategoryData
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class OnboardingDetailState(
    val currency: MysaveCurrency,
    val accounts: ImmutableList<AccountBalance>,
    val accountSuggestions: ImmutableList<CreateAccountData>,
    val categories: ImmutableList<Category>,
    val categorySuggestions: ImmutableList<CreateCategoryData>
)
