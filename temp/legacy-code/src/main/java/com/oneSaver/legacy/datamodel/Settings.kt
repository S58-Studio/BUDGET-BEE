package com.oneSaver.legacy.datamodel

import androidx.compose.runtime.Immutable
import com.oneSaver.base.legacy.Theme
import com.oneSaver.data.database.entities.SettingsEntity
import java.math.BigDecimal
import java.util.UUID

@Deprecated("Legacy data model. Will be deleted")
@Immutable
data class Settings(
    val theme: Theme,
    val baseCurrency: String,
    val bufferAmount: BigDecimal,
    val name: String,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): SettingsEntity = SettingsEntity(
        theme = theme,
        currency = baseCurrency,
        bufferAmount = bufferAmount.toDouble(),
        name = name,
        id = id
    )
}
