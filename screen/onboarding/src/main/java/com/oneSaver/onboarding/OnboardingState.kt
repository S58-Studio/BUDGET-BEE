package com.oneSaver.onboarding

import androidx.compose.runtime.Immutable

@Immutable
enum class OnboardingState {
    SPLASH,
    LOGIN,
    CHOOSE_PATH,
    CURRENCY,
    ACCOUNTS,
    CATEGORIES
}
