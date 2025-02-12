package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.SettingsEntity
import com.oneSaver.legacy.datamodel.Settings

fun SettingsEntity.toLegacyDomain(): Settings = Settings(
    theme = theme,
    baseCurrency = currency,
    bufferAmount = bufferAmount.toBigDecimal(),
    name = name,
    id = id
)
