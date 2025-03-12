package com.oneSaver.exchangerates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import arrow.core.raise.either
import com.oneSaver.base.Toaster
import com.oneSaver.base.threading.DispatchersProvider
import com.oneSaver.data.model.ExchangeRate
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.PositiveDouble
import com.oneSaver.data.repository.CurrencyRepository
import com.oneSaver.data.repository.ExchangeRatesRepository
import com.oneSaver.domains.usecase.exchange.SyncXchangeRatesUseCase
import com.oneSaver.exchangerates.data.RatingUI
import com.oneSaver.userInterface.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Stable
@HiltViewModel
class XchangeRatesVM @Inject constructor(
    private val syncXchangeRatesUseCase: SyncXchangeRatesUseCase,
    private val currencyRepo: CurrencyRepository,
    private val exchangeRatesRepo: ExchangeRatesRepository,
    private val dispatchers: DispatchersProvider,
    private val toaster: Toaster,
) : ComposeViewModel<CurrencyRateState, CurrencyRatEvent>() {
    private var searchQuery by mutableStateOf("")
    private var baseCurrency by mutableStateOf<AssetCode?>(null)

    private fun toUi(exchangeRate: ExchangeRate): RatingUI = RatingUI(
        from = exchangeRate.baseCurrency.code,
        to = exchangeRate.currency.code,
        rate = exchangeRate.rate.value
    )

    @Composable
    override fun uiState(): CurrencyRateState {
        LaunchedEffect(Unit) {
            baseCurrency = currencyRepo.getBaseCurrency().also {
                viewModelScope.launch {
                    syncXchangeRatesUseCase.sync(it)
                }
            }
        }

        val rates = getRates()

        return CurrencyRateState(
            baseCurrency = baseCurrency?.code ?: "",
            manual = rates.filter { it.manualOverride }.map(::toUi).toImmutableList(),
            automatic = rates.filter { !it.manualOverride }.map(::toUi).toImmutableList()
        )
    }

    @Composable
    private fun getRates(): List<ExchangeRate> {
        val rates by remember { exchangeRatesRepo.findAll() }
            .collectAsState(initial = emptyList())

        return rates.filter {
            if (searchQuery.isNotBlank()) {
                it.currency.code.contains(searchQuery, ignoreCase = true)
            } else {
                true
            }
        }.filter { baseCurrency == it.baseCurrency }
    }

    // region Event Handling
    override fun onEvent(event: CurrencyRatEvent) {
        viewModelScope.launch {
            when (event) {
                is CurrencyRatEvent.RemoveOverride -> handleRemoveOverride(event)
                is CurrencyRatEvent.Search -> handleSearch(event)
                is CurrencyRatEvent.UpdateRate -> handleUpdateRate(event)
                is CurrencyRatEvent.AddRate -> handleAddRate(event)
            }
        }
    }

    private suspend fun handleRemoveOverride(event: CurrencyRatEvent.RemoveOverride) {
        withContext(dispatchers.io) {
            either {
                exchangeRatesRepo.deleteByBaseCurrencyAndCurrency(
                    baseCurrency = AssetCode.from(event.rate.from).bind(),
                    currency = AssetCode.from(event.rate.to).bind()
                )
            }.onRight {
                // Sync to fetch the real rate
                baseCurrency?.let { syncXchangeRatesUseCase.sync(it) }
            }.onLeft { toaster.show(it) }
        }
    }

    private fun handleSearch(event: CurrencyRatEvent.Search) {
        searchQuery = event.query.trim()
    }

    private suspend fun handleUpdateRate(event: CurrencyRatEvent.UpdateRate) {
        withContext(dispatchers.io) {
            either {
                ExchangeRate(
                    baseCurrency = AssetCode.from(event.rate.from).bind(),
                    currency = AssetCode.from(event.rate.to).bind(),
                    rate = PositiveDouble.from(event.newRate).bind(),
                    manualOverride = true
                )
            }.onRight {
                exchangeRatesRepo.save(it)
            }.onLeft { toaster.show(it) }
        }
    }

    private suspend fun handleAddRate(event: CurrencyRatEvent.AddRate) {
        withContext(dispatchers.io) {
            either {
                ExchangeRate(
                    baseCurrency = AssetCode.from(event.rate.from).bind(),
                    currency = AssetCode.from(event.rate.to).bind(),
                    rate = PositiveDouble.from(event.rate.rate).bind(),
                    manualOverride = true
                )
            }.onRight {
                exchangeRatesRepo.save(it)
            }.onLeft { toaster.show(it) }
        }
    }
    // endregion
}
