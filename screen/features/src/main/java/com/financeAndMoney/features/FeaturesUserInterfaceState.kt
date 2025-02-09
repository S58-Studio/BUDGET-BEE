package com.financeAndMoney.features

import kotlinx.collections.immutable.ImmutableList

data class FeaturesUserInterfaceState(
    val features: ImmutableList<FeatureUi>,
)

data class FeatureUi(
    val name: String,
    val enabled: Boolean,
    val description: String?,
)
