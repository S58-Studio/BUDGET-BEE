package com.financeAndMoney.legacy.datamodel.temp

import com.financeAndMoney.data.database.entities.MkopoRecordEntity
import com.financeAndMoney.legacy.datamodel.LoanRecord

fun MkopoRecordEntity.toLegacyDomain(): LoanRecord = LoanRecord(
    loanId = loanId,
    amount = amount,
    note = note,
    dateTime = dateTime,
    interest = interest,
    accountId = accountId,
    convertedAmount = convertedAmount,
    loanRecordType = loanRecordType,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)
