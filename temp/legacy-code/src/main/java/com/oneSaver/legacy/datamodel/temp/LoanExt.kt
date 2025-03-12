package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.LoanEntity
import com.oneSaver.base.model.LoanType
import com.oneSaver.legacy.datamodel.Loan

fun LoanEntity.toLegacyDomain(): Loan = Loan(
    name = name,
    amount = amount,
    type = type,
    color = color,
    icon = icon,
    orderNum = orderNum,
    accountId = accountId,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id,
    dateTime = dateTime
)

fun LoanEntity.humanReadableType(): String {
    return if (type == LoanType.BORROW) "BORROWED" else "LENT"
}
