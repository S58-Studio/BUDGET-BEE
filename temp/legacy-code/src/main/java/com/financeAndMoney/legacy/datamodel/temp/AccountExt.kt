package com.financeAndMoney.legacy.datamodel.temp

import com.financeAndMoney.data.database.entities.AkauntiEntity
import com.financeAndMoney.legacy.datamodel.Account

fun AkauntiEntity.toLegacyDomain(): Account = Account(
    name = name,
    currency = currency,
    color = color,
    icon = icon,
    orderNum = orderNum,
    includeInBalance = includeInBalance,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)
