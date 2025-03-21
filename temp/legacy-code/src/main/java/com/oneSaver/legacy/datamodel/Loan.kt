package com.oneSaver.legacy.datamodel

import androidx.compose.runtime.Immutable
import com.oneSaver.data.database.entities.LoanEntity
import com.oneSaver.base.model.LoanType
import java.time.LocalDateTime
import java.util.UUID

@Deprecated("Legacy data model. Will be deleted")
@Immutable
data class Loan(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Int = 0,
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val accountId: UUID? = null,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val dateTime: LocalDateTime? = null,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): LoanEntity = LoanEntity(
        name = name,
        amount = amount,
        type = type,
        color = color,
        icon = icon,
        orderNum = orderNum,
        accountId = accountId,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id,
        dateTime = dateTime
    )
}
