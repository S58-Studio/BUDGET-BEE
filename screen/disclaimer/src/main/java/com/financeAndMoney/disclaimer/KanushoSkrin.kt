package com.financeAndMoney.disclaimer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financeAndMoney.disclaimer.composables.AcceptTermsText
import com.financeAndMoney.disclaimer.composables.AgreeButton
import com.financeAndMoney.disclaimer.composables.AgreementCheckBox
import com.financeAndMoney.disclaimer.composables.DisclaimerTopAppBar
import com.financeAndMoney.navigation.screenScopedViewModel
import com.financeAndMoney.userInterface.component.IntroducingMyLonAppCard

@Composable
fun KanushoScriniImplemation() {
    val viewModel: KanushoVM = screenScopedViewModel()
    val viewState = viewModel.uiState()
    KanushoSkriniUI(viewState = viewState, onEvent = viewModel::onEvent)
}

@Composable
fun KanushoSkriniUI(
    viewState: KanushoViewState,
    onEvent: (KanushoViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            DisclaimerTopAppBar()
        },
        content = { innerPadding ->
            Kontenti(
                modifier = Modifier.padding(innerPadding),
                viewState = viewState,
                onEvent = onEvent,
            )
        }
    )
}

@Composable
private fun Kontenti(
    viewState: KanushoViewState,
    onEvent: (KanushoViewEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        item {
            IntroducingMyLonAppCard()
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AcceptTermsText()
        }
        itemsIndexed(items = viewState.checkboxes) { index, item ->
            Spacer(modifier = Modifier.height(8.dp))
            AgreementCheckBox(
                viewState = item,
                onClick = {
                    onEvent(KanushoViewEvent.OnCheckboxClick(index))
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AgreeButton(
                enabled = viewState.agreeButtonEnabled,
            ) {

                onEvent(KanushoViewEvent.OnAgreeClick)
            }
        }
        item {
            // To ensure proper scrolling
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}