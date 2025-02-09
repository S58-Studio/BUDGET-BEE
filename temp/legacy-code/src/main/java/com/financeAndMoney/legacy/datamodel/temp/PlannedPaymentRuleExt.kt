package com.financeAndMoney.legacy.datamodel.temp

import com.financeAndMoney.data.database.entities.ScheduledPaymentRuleEntity
import com.financeAndMoney.legacy.datamodel.PlannedPaymentRule

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
