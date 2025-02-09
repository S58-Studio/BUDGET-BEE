package com.financeAndMoney.legacy.datamodel.temp

import com.financeAndMoney.data.database.entities.SettingsEntity
import com.financeAndMoney.legacy.datamodel.Settings

fun SettingsEntity.toLegacyDomain(): Settings = Settings(
    theme = theme,
    baseCurrency = currency,
    bufferAmount = bufferAmount.toBigDecimal(),
    name = name,
    id = id
)
