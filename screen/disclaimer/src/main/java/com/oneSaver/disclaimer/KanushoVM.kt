package com.oneSaver.disclaimer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oneSaver.data.repository.LegalRepository
import com.oneSaver.navigation.Navigation
import com.oneSaver.userInterface.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KanushoVM @Inject constructor(
    private val navigation: Navigation,
    private val legalRepo: LegalRepository,
) : ComposeViewModel<KanushoViewState, KanushoViewEvent>() {

    private var checkboxes by mutableStateOf(LegalCheckboxes)

    @Composable
    override fun uiState(): KanushoViewState {
        return KanushoViewState(
            checkboxes = checkboxes,
            agreeButtonEnabled = checkboxes.all(CheckboxViewState::checked),
        )
    }

    override fun onEvent(event: KanushoViewEvent) {
        when (event) {
            KanushoViewEvent.OnAgreeClick -> handleAgreeClick()
            is KanushoViewEvent.OnCheckboxClick -> handleCheckboxClick(event)
        }
    }

    private fun handleAgreeClick() {
        viewModelScope.launch {
            legalRepo.setDisclaimerAccepted(accepted = true)
            navigation.back()
        }
    }

    private fun handleCheckboxClick(event: KanushoViewEvent.OnCheckboxClick) {
        checkboxes = checkboxes.mapIndexed { index, item ->
            if (index == event.index) {
                item.copy(
                    checked = !item.checked
                )
            } else {
                item
            }
        }.toImmutableList()
    }

    companion object {
        // LEGAL text - do NOT extract or translate
        val LegalCheckboxes = listOf(
            CheckboxViewState(
                text = "I recognize this app, MySave is provided 'as-is' " +
                        "with no warranties, explicit or implied. " +
                        "I fully accept all risks of errors, defects, or failures, " +
                        "using the app solely at my own risk.",
                checked = false,
            ),
            CheckboxViewState(
                text = "I understand there is no warranty for the accuracy, " +
                        "reliability, or completeness of my data. " +
                        "Manual data backingUp is my Responsibility, and I agree to not hold " +
                        "the app,MySave liable for any data loss.",
                checked = false,
            ),
            CheckboxViewState(
                text = "I hereby release the app developers, " +
                        "and distributing company from any liability for " +
                        "claims, damages, legal fees, or losses, including those resulting " +
                        "from data inaccuracies.",
                checked = false,
            ),
            CheckboxViewState(
                text = "I am aware and accept that the app may display misleading information " +
                        "or contain inaccuracies. " +
                        "I assume full responsibility for verifying the integrity " +
                        "of financial data and calculations before making " +
                        "any decisions based on app data.",
                checked = false,
            ),
        ).toImmutableList()
    }
}