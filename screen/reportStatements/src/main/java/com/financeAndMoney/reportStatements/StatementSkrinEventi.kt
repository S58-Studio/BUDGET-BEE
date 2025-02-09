package com.financeAndMoney.reportStatements

import android.content.Context
import com.financeAndMoney.data.model.Transaction

sealed class StatementSkrinEventi {
    data class OnFilter(val filter: StatementsFilter?) : StatementSkrinEventi()
    data class OnExport(val context: Context) : StatementSkrinEventi()
    data class OnPayOrGet(val transaction: Transaction) : StatementSkrinEventi()
    data class SkipTransaction(val transaction: Transaction) : StatementSkrinEventi()
    data class SkipTransactions(val transactions: List<Transaction>) : StatementSkrinEventi()
    data class OnUpcomingExpanded(val upcomingExpanded: Boolean) : StatementSkrinEventi()
    data class OnOverdueExpanded(val overdueExpanded: Boolean) : StatementSkrinEventi()
    data class OnFilterOverlayVisible(val filterOverlayVisible: Boolean) : StatementSkrinEventi()
    data class OnTagSearch(val data: String) : StatementSkrinEventi()
    data class OnTreatTransfersAsIncomeExpense(val transfersAsIncomeExpense: Boolean) :
        StatementSkrinEventi()

    @Deprecated("Uses legacy Transaction")
    data class SkipTransactionsLegacy(val transactions: List<com.financeAndMoney.base.legacy.Transaction>) :
        StatementSkrinEventi()

    @Deprecated("Uses legacy Transaction")
    data class SkipTransactionLegacy(val transaction: com.financeAndMoney.base.legacy.Transaction) :
        StatementSkrinEventi()

    @Deprecated("Uses legacy Transaction")
    data class OnPayOrGetLegacy(val transaction: com.financeAndMoney.base.legacy.Transaction) :
        StatementSkrinEventi()
}
