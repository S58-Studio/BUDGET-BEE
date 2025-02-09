package com.financeAndMoney.home

import androidx.compose.runtime.Immutable
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.legacy.TransactionHistoryItem
import com.financeAndMoney.home.clientJourney.ClientJourneyCardModel
import com.financeAndMoney.legacy.data.AppBaseData
import com.financeAndMoney.legacy.data.BufferInfo
import com.financeAndMoney.legacy.data.LegacyDueSection
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.IncomeExpensePair
import kotlinx.collections.immutable.ImmutableList
import java.math.BigDecimal

@Immutable
data class NyumbaniState(
    val theme: Theme,
    val name: String,

    val period: TimePeriod,
    val baseData: AppBaseData,

    val history: ImmutableList<TransactionHistoryItem>,
    val stats: IncomeExpensePair,

    val balance: BigDecimal,

    val buffer: BufferInfo,

    val upcoming: LegacyDueSection,
    val overdue: LegacyDueSection,

    val customerJourneyCards: ImmutableList<ClientJourneyCardModel>,
    val hideBalance: Boolean,
    val hideIncome: Boolean,
    val expanded: Boolean
)
