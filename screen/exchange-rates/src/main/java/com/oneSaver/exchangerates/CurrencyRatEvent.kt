package com.oneSaver.exchangerates

import com.oneSaver.exchangerates.data.RatingUI

sealed interface CurrencyRatEvent {
    data class Search(val query: String) : CurrencyRatEvent
    data class RemoveOverride(val rate: RatingUI) : CurrencyRatEvent
    data class UpdateRate(val rate: RatingUI, val newRate: Double) : CurrencyRatEvent
    data class AddRate(val rate: RatingUI) : CurrencyRatEvent
}
