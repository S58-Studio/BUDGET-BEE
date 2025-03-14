package com.oneSaver.data.database.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oneSaver.base.kotlinxserilzation.KSerializerInstant
import com.oneSaver.base.kotlinxserilzation.KSerializerLocalDateTime
import com.oneSaver.base.kotlinxserilzation.KSerializerUUID
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.IntervalType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Suppress("DataClassDefaultValues")
@Keep
@Serializable
@Entity(tableName = "planned_payment_rules")
data class ScheduledPaymentRuleEntity(
    @SerialName("startDate")
    @Serializable(with = KSerializerInstant::class)
    val startDate: Instant?,
    @SerialName("intervalN")
    val intervalN: Int?,
    @SerialName("intervalType")
    val intervalType: IntervalType?,
    @SerialName("oneTime")
    val oneTime: Boolean,
    @SerialName("type")
    val type: TransactionType,
    @SerialName("accountId")
    @Serializable(with = KSerializerUUID::class)
    val accountId: UUID,
    @SerialName("amount")
    val amount: Double = 0.0,
    @SerialName("categoryId")
    @Serializable(with = KSerializerUUID::class)
    val categoryId: UUID? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,

    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID()
)
