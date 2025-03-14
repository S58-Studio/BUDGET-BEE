package com.oneSaver.piechart

import androidx.compose.runtime.Immutable
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.base.model.TransactionType
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.allStatus.userInterface.theme.modal.ChoosePeriodModalData
import kotlinx.collections.immutable.ImmutableList
import java.util.UUID

@Immutable
data class FinPieChartStatisticState(
    val transactionType: TransactionType,
    val period: TimePeriod,
    val baseCurrency: String,
    val totalAmount: Double,
    val kategoriAmounts: ImmutableList<KategoriAmount>,
    val selectedKategori: SelectedKategori?,
    val accountIdFilterList: ImmutableList<UUID>,
    val showCloseButtonOnly: Boolean,
    val filterExcluded: Boolean,
    val transactions: ImmutableList<Transaction>,
    val choosePeriodModal: ChoosePeriodModalData?
)
