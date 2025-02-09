package com.financeAndMoney.data.backingUp

import kotlinx.collections.immutable.ImmutableList

data class ImportingResults(
    val rowsFound: Int,
    val transactionsImported: Int,
    val accountsImported: Int,
    val categoriesImported: Int,
    val failedRows: ImmutableList<CSVRow>,
)
