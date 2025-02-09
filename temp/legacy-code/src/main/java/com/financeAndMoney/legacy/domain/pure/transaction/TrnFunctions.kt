package com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.financeAndMoney.base.model.TransactionType
import com.financeAndMoney.base.time.convertToLocal
import com.financeAndMoney.data.model.Expense
import com.financeAndMoney.data.model.Income
import com.financeAndMoney.data.model.Transaction
import com.financeAndMoney.data.model.Transfer
import com.financeAndMoney.data.temp.migration.getAccountId

import com.financeAndMoney.frp.Pure
import com.financeAndMoney.legacy.datamodel.Account
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.account.accountCurrency
import java.time.LocalDate

@Pure
fun expenses(transactions: List<Transaction>): List<Transaction> {
    return transactions.filterIsInstance<Expense>()
}

@Pure
fun incomes(transactions: List<Transaction>): List<Transaction> {
    return transactions.filterIsInstance<Income>()
}

@Pure
fun transfers(transactions: List<Transaction>): List<Transaction> {
    return transactions.filterIsInstance<Transfer>()
}

@Pure
fun isUpcoming(transaction: Transaction, dateNow: LocalDate): Boolean {
    val dueDate = transaction.time.convertToLocal().toLocalDate() ?: return false
    return dateNow.isBefore(dueDate) || dateNow.isEqual(dueDate)
}

@Pure
fun isOverdue(transaction: Transaction, dateNow: LocalDate): Boolean {
    val dueDate = transaction.time.convertToLocal().toLocalDate() ?: return false
    return dateNow.isAfter(dueDate)
}

@Pure
fun trnCurrency(
    transaction: Transaction,
    accounts: List<Account>,
    baseCurrency: String
): Option<String> {
    val account = accounts.find {
        it.id == transaction.getAccountId()
    }
        ?: return baseCurrency.toOption()
    return accountCurrency(account, baseCurrency).toOption()
}

@Deprecated("Uses legacy Transaction")
object LegacyTrnFunctions {
    @Pure
    fun expenses(transactions: List<com.financeAndMoney.base.legacy.Transaction>): List<com.financeAndMoney.base.legacy.Transaction> {
        return transactions.filter { it.type == TransactionType.EXPENSE }
    }

    @Pure
    fun incomes(transactions: List<com.financeAndMoney.base.legacy.Transaction>): List<com.financeAndMoney.base.legacy.Transaction> {
        return transactions.filter { it.type == TransactionType.INCOME }
    }

    @Pure
    fun trnCurrency(
        transaction: com.financeAndMoney.base.legacy.Transaction,
        accounts: List<Account>,
        baseCurrency: String
    ): Option<String> {
        val account = accounts.find { it.id == transaction.accountId }
            ?: return baseCurrency.toOption()
        return accountCurrency(account, baseCurrency).toOption()
    }
}
