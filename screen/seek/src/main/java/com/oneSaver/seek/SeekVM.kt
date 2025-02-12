package com.oneSaver.seek

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.userInterface.ComposeViewModel
import com.oneSaver.data.model.Category
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.utils.getDefaultFIATCurrency
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.allStatus.domain.action.account.AccountsAct
import com.oneSaver.allStatus.domain.action.settings.BaseCurrencyAct
import com.oneSaver.allStatus.domain.action.transaction.AllTrnsAct
import com.oneSaver.allStatus.domain.action.transaction.TrnsWithDateDivsAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class SeekVM @Inject constructor(
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val accountsAct: AccountsAct,
    private val categoryRepository: CategoryRepository,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val allTrnsAct: AllTrnsAct
) : ComposeViewModel<SeekState, SeekEvent>() {

    private val transactions =
        mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())
    private val baseCurrency = mutableStateOf<String>(getDefaultFIATCurrency().currencyCode)
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val searchQuery = mutableStateOf("")

    @Composable
    override fun uiState(): SeekState {
        LaunchedEffect(Unit) {
            search(searchQuery.value)
        }

        return SeekState(
            searchQuery = searchQuery.value,
            transactions = transactions.value,
            baseCurrency = baseCurrency.value,
            accounts = accounts.value,
            categories = categories.value
        )
    }

    override fun onEvent(event: SeekEvent) {
        when (event) {
            is SeekEvent.Seek -> search(event.query)
        }
    }

    private fun search(query: String) {
        searchQuery.value = query
        val normalizedQuery = query.lowercase().trim()

        viewModelScope.launch {
            val queryResult = ioThread {
                val filteredTransactions = allTrnsAct(Unit)
                    .filter { transaction ->
                        transaction.title.matchesQuery(normalizedQuery) ||
                                transaction.description.matchesQuery(normalizedQuery)
                    }
                trnsWithDateDivsAct(
                    TrnsWithDateDivsAct.Input(
                        baseCurrency = baseCurrencyAct(Unit),
                        transactions = filteredTransactions
                    )
                ).toImmutableList()
            }

            transactions.value = queryResult
            baseCurrency.value = baseCurrencyAct(Unit)
            accounts.value = accountsAct(Unit)
            categories.value = categoryRepository.findAll().toImmutableList()
        }
    }

    private fun NotBlankTrimmedString?.matchesQuery(query: String): Boolean {
        return this?.value?.lowercase()?.contains(query) == true
    }
}
