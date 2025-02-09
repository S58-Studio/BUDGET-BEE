package com.financeAndMoney.seek

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.design.utils.IvyComponentPreview
import com.financeAndMoney.legacy.data.AppBaseData
import com.financeAndMoney.legacy.ui.SearchInput
import com.financeAndMoney.legacy.ui.component.transaction.transactions
import com.financeAndMoney.legacy.utils.densityScope
import com.financeAndMoney.legacy.utils.keyboardOnlyWindowInsets
import com.financeAndMoney.legacy.utils.keyboardVisibleState
import com.financeAndMoney.legacy.utils.selectEndTextFieldValue
import com.financeAndMoney.navigation.MylonPreview
import com.financeAndMoney.navigation.SeekSkrin
import com.financeAndMoney.navigation.screenScopedViewModel
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.expenseAndBudgetPlanner.userInterface.theme.modal.DURATION_MODAL_ANIM
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SeekSkrin(screen: SeekSkrin) {
    val viewModel: SeekVM = screenScopedViewModel()
    val uiState = viewModel.uiState()

    SeekUI(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun SeekUI(
    uiState: SeekState,
    onEvent: (SeekEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Spacer(Modifier.height(24.dp))

        val listState = rememberLazyListState()

        var searchQueryTextFieldValue by remember {
            mutableStateOf(selectEndTextFieldValue(uiState.searchQuery))
        }

        SearchInput(
            searchQueryTextFieldValue = searchQueryTextFieldValue,
            hint = stringResource(R.string.search_transactions),
            onSetSearchQueryTextField = {
                searchQueryTextFieldValue = it
                onEvent(SeekEvent.Seek(it.text))
            }
        )

        LaunchedEffect(uiState.transactions) {
            // scroll to top when transfers are changed
            listState.animateScrollToItem(index = 0, scrollOffset = 0)
        }

        Spacer(Modifier.height(16.dp))
        val emptyStateTitle = stringResource(R.string.no_transactions)
        val emptyStateText = stringResource(
            R.string.no_transactions_for_query,
            searchQueryTextFieldValue.text
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState

        ) {
            transactions(
                baseData = AppBaseData(
                    baseCurrency = uiState.baseCurrency,
                    accounts = uiState.accounts,
                    categories = uiState.categories
                ),
                upcoming = null,
                setUpcomingExpanded = { },
                overdue = null,
                setOverdueExpanded = { },
                history = uiState.transactions,
                onPayOrGet = { },
                emptyStateTitle = emptyStateTitle,
                emptyStateText = emptyStateText,
                dateDividerMarginTop = 16.dp
            )

            item {
                val keyboardVisible by keyboardVisibleState()
                val keyboardShownInsetDp by animateDpAsState(
                    targetValue = densityScope {
                        if (keyboardVisible) keyboardOnlyWindowInsets().bottom.toDp() else 0.dp
                    },
                    animationSpec = tween(DURATION_MODAL_ANIM)
                )

                Spacer(Modifier.height(keyboardShownInsetDp))
                // add keyboard height margin at bototm so the list can scroll to bottom
            }
        }
    }
}

@Preview
@Composable
private fun Preview(isDark: Boolean = false) {
    MylonPreview(isDark) {
        SeekUI(
            uiState = SeekState(
                searchQuery = "Transaction",
                transactions = persistentListOf(),
                baseCurrency = "",
                accounts = persistentListOf(),
                categories = persistentListOf()
            ),
            onEvent = {}
        )
    }
}

/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeekUiTest(isDark: Boolean) {
    val theme = if (isDark) Theme.DARK else Theme.LIGHT
    IvyComponentPreview(theme = theme) {
        Preview(isDark)
    }
}