package com.financeAndMoney.data.backingUp

import androidx.annotation.Keep
import com.financeAndMoney.data.database.entities.AkauntiEntity
import com.financeAndMoney.data.database.entities.BajetiEntity
import com.financeAndMoney.data.database.entities.KategoriEntity
import com.financeAndMoney.data.database.entities.MkopoEntity
import com.financeAndMoney.data.database.entities.MkopoRecordEntity
import com.financeAndMoney.data.database.entities.ScheduledPaymentRuleEntity
import com.financeAndMoney.data.database.entities.SettingsEntity
import com.financeAndMoney.data.database.entities.TransactionEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MylonWalletCompleteData(
    @SerialName("accounts")
    val accounts: List<AkauntiEntity> = emptyList(),
    @SerialName("budgets")
    val budgets: List<BajetiEntity> = emptyList(),
    @SerialName("categories")
    val categories: List<KategoriEntity> = emptyList(),
    @SerialName("loanRecords")
    val loanRecords: List<MkopoRecordEntity> = emptyList(),
    @SerialName("loans")
    val loans: List<MkopoEntity> = emptyList(),
    @SerialName("plannedPaymentRules")
    val plannedPaymentRules: List<ScheduledPaymentRuleEntity> = emptyList(),
    @SerialName("controls")
    val settings: List<SettingsEntity> = emptyList(),
    @SerialName("transfers")
    val transactions: List<TransactionEntity> = emptyList(),
    @SerialName("sharedPrefs")
    val sharedPrefs: HashMap<String, String> = HashMap()
)
