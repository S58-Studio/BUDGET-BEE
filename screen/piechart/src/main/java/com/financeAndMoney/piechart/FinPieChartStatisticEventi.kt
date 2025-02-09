package com.financeAndMoney.piechart

import com.financeAndMoney.data.model.Category
import com.financeAndMoney.legacy.data.model.TimePeriod
import com.financeAndMoney.navigation.FinPieChartStatisticSkrin

sealed interface FinPieChartStatisticEventi {
    data class OnStart(val screen: FinPieChartStatisticSkrin) : FinPieChartStatisticEventi
    data object OnSelectNextMonth : FinPieChartStatisticEventi
    data object OnSelectPreviousMonth : FinPieChartStatisticEventi
    data class OnSetPeriod(val timePeriod: TimePeriod) : FinPieChartStatisticEventi
    data class OnCategoryClicked(val category: Category?) : FinPieChartStatisticEventi
    data class OnShowMonthModal(val timePeriod: TimePeriod?) : FinPieChartStatisticEventi
}
