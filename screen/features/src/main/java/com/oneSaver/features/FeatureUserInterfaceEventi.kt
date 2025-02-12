package com.oneSaver.features

sealed interface FeatureUserInterfaceEventi {
    data class ToggleFeature(val index: Int) : FeatureUserInterfaceEventi
}
