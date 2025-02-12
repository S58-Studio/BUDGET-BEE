package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.BajetiEntity
import com.oneSaver.legacy.datamodel.Budget
import java.util.UUID

fun BajetiEntity.toLegacyDomain(): Budget = Budget(
    name = name,
    amount = amount,
    categoryIdsSerialized = categoryIdsSerialized,
    accountIdsSerialized = accountIdsSerialized,
    isSynced = isSynced,
    isDeleted = isDeleted,
    orderId = orderId,
    id = id
)

fun serialize(ids: List<UUID>): String {
    return ids.joinToString(separator = ",")
}

fun budgetType(categoriesCount: Int): String {
    return when (categoriesCount) {
        0 -> "Total Budget"
        1 -> "Category Budget"
        else -> "Multi-Category ($categoriesCount) Budget"
    }
}

fun BajetiEntity.parseCategoryIds(): List<UUID> {
    return parseIdsString(categoryIdsSerialized)
}

fun BajetiEntity.parseAccountIds(): List<UUID> {
    return parseIdsString(accountIdsSerialized)
}

private fun parseIdsString(idsString: String?): List<UUID> {
    return try {
        if (idsString == null) return emptyList()

        idsString
            .split(",")
            .map { UUID.fromString(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun BajetiEntity.validate(): Boolean {
    return name.isNotEmpty() && amount > 0.0
}
