package com.oneSaver.home

import androidx.compose.runtime.Immutable
import com.oneSaver.base.legacy.Theme
import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.home.clientJourney.ClientJourneyCardModel
import com.oneSaver.legacy.data.AppBaseData
import com.oneSaver.legacy.data.BufferInfo
import com.oneSaver.legacy.data.LegacyDueSection
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.allStatus.domain.pure.data.IncomeExpensePair
import kotlinx.collections.immutable.ImmutableList
import java.math.BigDecimal

@Immutable
data class HomeState(
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
    val expanded: Boolean,
    val shouldShowAccountSpecificColorInTransactions: Boolean
)

