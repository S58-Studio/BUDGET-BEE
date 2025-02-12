package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.AkauntiEntity
import com.oneSaver.legacy.datamodel.Account

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
