package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.ScheduledPaymentRuleEntity
import com.oneSaver.legacy.datamodel.PlannedPaymentRule

fun ScheduledPaymentRuleEntity.toLegacyDomain(): PlannedPaymentRule = PlannedPaymentRule(
    startDate = startDate,
    intervalN = intervalN,
    intervalType = intervalType,
    oneTime = oneTime,
    type = type,
    accountId = accountId,
    amount = amount,
    categoryId = categoryId,
    title = title,
    description = description,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)
