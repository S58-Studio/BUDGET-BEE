package com.financeAndMoney.data.temp.migration

import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.data.model.Expense
import com.financeAndMoney.data.model.Income
import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.model.Transfer
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

fun Transaction.getValue(): BigDecimal = when (this) {
    is Expense -> value.amount.value.toBigDecimal()
    is Income -> value.amount.value.toBigDecimal()
    is Transfer -> fromValue.amount.value.toBigDecimal()
}

fun Transaction.getAccountId(): UUID = when (this) {
    is Expense -> account.value
    is Income -> account.value
    is Transfer -> fromAccount.value
}

fun Transaction.getTransactionType(): TransactionType = when (this) {
    is Expense -> TransactionType.EXPENSE
    is Income -> TransactionType.INCOME
    is Transfer -> TransactionType.TRANSFER
}

fun Transaction.settleNow(): Transaction {
    val timeNow = Instant.now()
    return when (this) {
        is Income -> this.copy(
            settled = true,
            time = timeNow
        )

        is Expense -> this.copy(
            settled = true,
            time = timeNow
        )

        is Transfer -> this.copy(
            settled = true,
            time = timeNow
        )
    }
}