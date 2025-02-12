package com.oneSaver.transfers

import androidx.compose.runtime.Immutable
import com.oneSaver.base.legacy.Transaction
import com.oneSaver.base.legacy.TransactionHistoryItem
import com.oneSaver.data.model.Category
import com.oneSaver.legacy.data.model.TimePeriod
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.allStatus.userInterface.theme.modal.ChoosePeriodModalData
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class TransferState(
    val period: TimePeriod,
    val baseCurrency: String,
    val currency: String,
    val categories: ImmutableList<Category>,
    val accounts: ImmutableList<Account>,
    val account: Account?,
    val category: Category?,
    val balance: Double,
    val balanceBaseCurrency: Double?,
    val income: Double,
    val expenses: Double,
    val initWithTransactions: Boolean,
    val treatTransfersAsIncomeExpense: Boolean,
    val history: ImmutableList<TransactionHistoryItem>,
    val upcoming: ImmutableList<Transaction>,
    val upcomingExpanded: Boolean,
    val upcomingIncome: Double,
    val upcomingExpenses: Double,
    val overdue: ImmutableList<Transaction>,
    val overdueExpanded: Boolean,
    val overdueIncome: Double,
    val overdueExpenses: Double,
    val enableDeletionButton: Boolean,
    val skipAllModalVisible: Boolean,
    val deleteModal1Visible: Boolean,
    val choosePeriodModal: ChoosePeriodModalData?,
)
