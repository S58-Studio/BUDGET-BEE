package com.financeAndMoney.navigation

import com.financeAndMoney.base.legacy.Transaction
import com.financeAndMoney.base.model.TransactionType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

data object MainSkreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object OnboardingScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class CSVScreen(
    val launchedFromOnboarding: Boolean
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class ModifyTransactionSkrin(
    val initialTransactionId: UUID?,
    val type: TransactionType,
    // extras
    val accountId: UUID? = null,
    val categoryId: UUID? = null
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class TransactScrin(
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val unspecifiedCategory: Boolean? = false,
    val transactionType: TransactionType? = null,
    val accountIdFilterList: List<UUID> = persistentListOf(),
    val transactions: List<Transaction> = persistentListOf()
) : Screen

data class FinPieChartStatisticSkrin(
    val type: TransactionType,
    val filterExcluded: Boolean = true,
    val accountList: ImmutableList<UUID> = persistentListOf(),
    val transactions: ImmutableList<Transaction> = persistentListOf(),
    val treatTransfersAsIncomeExpense: Boolean = false
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class ModifyScheduledSkrin(
    val plannedPaymentRuleId: UUID?,
    val type: TransactionType,
    val amount: Double? = null,
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val title: String? = null,
    val description: String? = null,
) : Screen {
    override val isLegacy: Boolean
        get() = true

    fun mandatoryFilled(): Boolean {
        return amount != null && amount > 0.0 &&
            accountId != null
    }
}

data object BalanceSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object ScheduledPaymntsSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object KategoriSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}
data object AkauntiTabSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}
data object SettingSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class ImportingSkrin(
    val launchedFromOnboarding: Boolean
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

//data object ReportScreen : Screen {
//    override val isLegacy: Boolean
//        get() = true
//}

data object BajetiSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object MkopoSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object SeekSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class MkopoDetailsSkrin(
    val loanId: UUID
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object XchangeRatesSkrin : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object FeatureSkrin : Screen

data object DisclaimerScreen : Screen
