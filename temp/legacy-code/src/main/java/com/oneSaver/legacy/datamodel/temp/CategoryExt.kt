package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.KategoriEntity
import com.oneSaver.legacy.datamodel.Category

fun KategoriEntity.toLegacyDomain(): Category = Category(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)
