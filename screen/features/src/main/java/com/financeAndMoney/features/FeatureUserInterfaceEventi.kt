package com.financeAndMoney.features

sealed interface FeatureUserInterfaceEventi {
    data class ToggleFeature(val index: Int) : FeatureUserInterfaceEventi
}
