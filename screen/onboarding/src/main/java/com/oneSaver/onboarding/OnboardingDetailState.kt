package com.oneSaver.onboarding

import androidx.compose.runtime.Immutable
import com.oneSaver.data.model.Category
import com.oneSaver.legacy.data.model.AccountBalance
import com.oneSaver.legacy.domain.data.MysaveCurrency
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateCategoryData
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class OnboardingDetailState(
    val currency: MysaveCurrency,
    val accounts: ImmutableList<AccountBalance>,
    val accountSuggestions: ImmutableList<CreateAccountData>,
    val categories: ImmutableList<Category>,
    val categorySuggestions: ImmutableList<CreateCategoryData>
)
