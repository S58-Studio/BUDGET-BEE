package com.oneSaver.features

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.domains.features.Features
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class FeaturesVM @Inject constructor(
    private val features: Features,
    @ApplicationContext
    private val context: Context
) : ComposeViewModel<FeaturesUserInterfaceState, FeatureUserInterfaceEventi>() {
    @Composable
    override fun uiState(): FeaturesUserInterfaceState {
        return FeaturesUserInterfaceState(
            features = getFeatures()
        )
    }

    @Composable
    fun getFeatures(): ImmutableList<FeatureUi> {
        val allFeatures = features.allFeatures.map {
            FeatureUi(
                name = it.name ?: it.key,
                description = it.description,
                enabled = it.asEnabledState()
            )
        }
        return allFeatures.toImmutableList()
    }

    override fun onEvent(event: FeatureUserInterfaceEventi) {
        when (event) {
            is FeatureUserInterfaceEventi.ToggleFeature -> toggleFeature(event)
        }
    }

    private fun toggleFeature(event: FeatureUserInterfaceEventi.ToggleFeature) {
        viewModelScope.launch {
            val feature = features.allFeatures[event.index]
            val enabled = feature.enabled(context).first() ?: false
            feature.set(context, !enabled)
        }
    }
}
