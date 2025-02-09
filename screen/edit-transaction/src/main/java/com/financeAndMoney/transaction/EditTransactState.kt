package com.financeAndMoney.transaction

import androidx.compose.runtime.Immutable
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.data.model.Category
import com.financeAndMoney.data.model.Tag
import com.financeAndMoney.data.model.TagId
import com.financeAndMoney.legacy.data.EditTransactionDisplayLoan
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.expenseAndBudgetPlanner.domain.data.CustomExchangeRateState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import java.time.LocalDateTime

@Immutable
data class EditTransactState(
    val transactionType: TransactionType,
    val initialTitle: String?,
    val titleSuggestions: ImmutableSet<String>,
    val currency: String,
    val description: String?,
    val dateTime: LocalDateTime?,
    val dueDate: LocalDateTime?,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>,
    val account: Account?,
    val toAccount: Account?,
    val category: Category?,
    val amount: Double,
    val hasChanges: Boolean,
    val displayLoanHelper: EditTransactionDisplayLoan,
    val backgroundProcessingStarted: Boolean,
    val customExchangeRateState: CustomExchangeRateState,
    val tags: ImmutableList<Tag>,
    val transactionAssociatedTags: ImmutableList<TagId>
)
