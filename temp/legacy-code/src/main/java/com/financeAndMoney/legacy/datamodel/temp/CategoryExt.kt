package com.financeAndMoney.legacy.datamodel.temp

import com.financeAndMoney.data.database.entities.KategoriEntity
import com.financeAndMoney.legacy.datamodel.Category

fun KategoriEntity.toLegacyDomain(): Category = Category(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)
