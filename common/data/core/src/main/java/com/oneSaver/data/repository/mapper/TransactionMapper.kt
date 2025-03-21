package com.oneSaver.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.oneSaver.base.TimeProvider
import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.database.entities.TransactionEntity
import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.Expense
import com.oneSaver.data.model.Income
import com.oneSaver.data.model.PositiveValue
import com.oneSaver.data.model.TagId
import com.oneSaver.data.model.Transaction
import com.oneSaver.data.model.TransactionId
import com.oneSaver.data.model.TransactionMetadata
import com.oneSaver.data.model.Transfer
import com.oneSaver.data.model.getFromAccount
import com.oneSaver.data.model.getToAccount
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.model.primitive.PositiveDouble
import com.oneSaver.data.repository.AccountRepository
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

class TransactionMapper @Inject constructor(
    private val accountRepository: AccountRepository,
) {

    suspend fun TransactionEntity.toDomain(
        tags: List<TagId> = emptyList()
    ): Either<String, Transaction> = either {
        ensure(!isDeleted) { "Transaction is deleted" }

        val metadata = TransactionMetadata(
            recurringRuleId = recurringRuleId,
            paidForDateTime = paidForDateTime,
            loanId = loanId,
            loanRecordId = loanRecordId
        )

        val settled = dateTime != null
        val time = mapTime().bind()

        val accountId = AccountId(accountId)
        val sourceAccount = accountRepository.findById(accountId)
        ensureNotNull(sourceAccount) { "No source account for transaction: ${this@toDomain}" }
        val fromValue = PositiveValue(
            amount = PositiveDouble.from(amount).bind(),
            asset = sourceAccount.asset
        )

        val notBlankTitle = title?.let(NotBlankTrimmedString::from)?.getOrNull()
        val notBlankDescription = description?.let(NotBlankTrimmedString::from)?.getOrNull()
        val category = categoryId?.let(::CategoryId)
        val transactionId = TransactionId(id)

        when (type) {
            TransactionType.INCOME -> {
                Income(
                    id = transactionId,
                    value = fromValue,
                    account = accountId,
                    title = notBlankTitle,
                    description = notBlankDescription,
                    category = category,
                    time = time,
                    settled = settled,
                    metadata = metadata,
                    tags = tags,
                )
            }

            TransactionType.EXPENSE -> {
                Expense(
                    id = transactionId,
                    account = accountId,
                    value = fromValue,
                    title = notBlankTitle,
                    description = notBlankDescription,
                    category = category,
                    time = time,
                    settled = settled,
                    metadata = metadata,
                    tags = tags,
                )
            }

            TransactionType.TRANSFER -> {
                val toAccountId = toAccountId?.let(::AccountId)
                ensureNotNull(toAccountId) {
                    "No destination account id associated with transaction '${this@toDomain}'"
                }
                ensure(accountId != toAccountId) {
                    "Self transfers aren't allowed. Source and destination accounts " +
                            "must be different for transaction: ${this@toDomain}"
                }

                val toAccount = accountRepository.findById(toAccountId)
                ensureNotNull(toAccount) {
                    "No destination account associated with transaction '${this@toDomain}'"
                }

                val toValue = PositiveValue(
                    amount = toAmount?.let(PositiveDouble::from)?.getOrNull()
                        ?: fromValue.amount,
                    asset = toAccount.asset
                )

                Transfer(
                    id = transactionId,
                    title = notBlankTitle,
                    description = notBlankDescription,
                    category = category,
                    time = time,
                    settled = settled,
                    metadata = metadata,
                    fromAccount = accountId,
                    fromValue = fromValue,
                    toAccount = toAccountId,
                    toValue = toValue,
                    tags = tags,
                )
            }
        }
    }

    private fun TransactionEntity.mapTime(): Either<String, Instant> = either {
        val time = (dateTime ?: dueDate)
        ensureNotNull(time) { "Missing transaction time for entity: $this" }
        time
    }

    fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            accountId = getFromAccount().value,
            type = when (this) {
                is Expense -> TransactionType.EXPENSE
                is Income -> TransactionType.INCOME
                is Transfer -> TransactionType.TRANSFER
            },
            amount = when (this) {
                is Expense -> value.amount.value
                is Income -> value.amount.value
                is Transfer -> fromValue.amount.value
            },
            toAccountId = getToAccount()?.value,
            toAmount = if (this is Transfer) {
                toValue.amount.value
            } else {
                null
            },
            title = title?.value,
            description = description?.value,
            dateTime = time.takeIf { settled },
            categoryId = category?.value,
            dueDate = time.takeIf { !settled },
            paidForDateTime = metadata.paidForDateTime,
            recurringRuleId = metadata.recurringRuleId,
            attachmentUrl = null,
            loanId = metadata.loanId,
            loanRecordId = metadata.loanRecordId,
            isSynced = true,
            isDeleted = false,
            id = id.value
        )
    }
}