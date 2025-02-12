package com.oneSaver.data.database.entities

import androidx.annotation.Keep
import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Entity(tableName = "exchange_rates", primaryKeys = ["baseCurrency", "currency"])
data class XchangeRateEntity(
    @SerialName("baseCurrency")
    val baseCurrency: String,
    @SerialName("currency")
    val currency: String,
    @SerialName("rate")
    val rate: Double,
    @SerialName("manualOverride")
    val manualOverride: Boolean = false,
)
