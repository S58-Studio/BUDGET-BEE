package com.oneSaver.transaction

import androidx.compose.runtime.Immutable
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.Tag
import com.oneSaver.data.model.TagId
import com.oneSaver.legacy.data.EditTransactionDisplayLoan
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.allStatus.domain.data.CustomExchangeRateState
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
