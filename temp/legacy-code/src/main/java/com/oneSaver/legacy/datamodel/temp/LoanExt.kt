package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.MkopoEntity
import com.oneSaver.data.model.LoanType
import com.oneSaver.legacy.datamodel.Loan

fun MkopoEntity.toLegacyDomain(): Loan = Loan(
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

fun MkopoEntity.humanReadableType(): String {
    return if (type == LoanType.BORROW) "BORROWED" else "LENT"
}
