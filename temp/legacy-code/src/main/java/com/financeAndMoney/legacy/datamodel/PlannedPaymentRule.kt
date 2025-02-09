package com.financeAndMoney.legacy.datamodel

import androidx.compose.runtime.Immutable
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.data.database.entities.ScheduledPaymentRuleEntity
import com.financeAndMoney.data.model.IntervalType
import java.time.LocalDateTime
import java.util.UUID

@Deprecated("Legacy data model. Will be deleted")
@Immutable
data class PlannedPaymentRule(
    val startDate: LocalDateTime?,
    val intervalN: Int?,
    val intervalType: IntervalType?,
    val oneTime: Boolean,

    val type: TransactionType,
    val accountId: UUID,
    val amount: Double = 0.0,
    val categoryId: UUID? = null,
    val title: String? = null,
    val description: String? = null,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): ScheduledPaymentRuleEntity = ScheduledPaymentRuleEntity(
        startDate = startDate,
        intervalN = intervalN,
        intervalType = intervalType,
        oneTime = oneTime,
        type = type,
        accountId = accountId,
        amount = amount,
        categoryId = categoryId,
        title = title,
        description = description,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )
}
