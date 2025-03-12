package com.oneSaver.data.backingUp

import androidx.annotation.Keep
import com.oneSaver.data.database.entities.AccountEntity
import com.oneSaver.data.database.entities.BudgetEntity
import com.oneSaver.data.database.entities.CategoryEntity
import com.oneSaver.data.database.entities.LoanEntity
import com.oneSaver.data.database.entities.LoanRecordEntity
import com.oneSaver.data.database.entities.ScheduledPaymentRuleEntity
import com.oneSaver.data.database.entities.SettingsEntity
import com.oneSaver.data.database.entities.TagAssociationEntity
import com.oneSaver.data.database.entities.TagEntity
import com.oneSaver.data.database.entities.TransactionEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MylonWalletCompleteData(
    @SerialName("accounts")
    val accounts: List<AccountEntity> = emptyList(),
    @SerialName("budgets")
    val budgets: List<BudgetEntity> = emptyList(),
    @SerialName("categories")
    val categories: List<CategoryEntity> = emptyList(),
    @SerialName("loanRecords")
    val loanRecords: List<LoanRecordEntity> = emptyList(),
    @SerialName("loans")
    val loans: List<LoanEntity> = emptyList(),
    @SerialName("plannedPaymentRules")
    val plannedPaymentRules: List<ScheduledPaymentRuleEntity> = emptyList(),
    @SerialName("settings")
    val settings: List<SettingsEntity> = emptyList(),
    @SerialName("transactions")
    val transactions: List<TransactionEntity> = emptyList(),
    @SerialName("sharedPrefs")
    val sharedPrefs: HashMap<String, String> = HashMap(),
    @SerialName("tags")
    val tags: List<TagEntity> = emptyList(),
    @SerialName("tagAssociations")
    val tagAssociations: List<TagAssociationEntity> = emptyList()
)
