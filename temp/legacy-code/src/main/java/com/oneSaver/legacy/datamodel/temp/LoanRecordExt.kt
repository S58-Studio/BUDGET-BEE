package com.oneSaver.legacy.datamodel.temp

import com.oneSaver.data.database.entities.MkopoRecordEntity
import com.oneSaver.legacy.datamodel.LoanRecord

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
