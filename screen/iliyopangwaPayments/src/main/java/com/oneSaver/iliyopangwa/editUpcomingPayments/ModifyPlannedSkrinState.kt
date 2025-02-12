package com.oneSaver.iliyopangwa.editUpcomingPayments

import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.Category
import com.oneSaver.data.model.IntervalType
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.allStatus.userInterface.theme.modal.RecurringRuleModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.AccountModalData
import com.oneSaver.allStatus.userInterface.theme.modal.edit.CategoryModalData
import kotlinx.collections.immutable.ImmutableList
import java.time.LocalDateTime
import javax.annotation.concurrent.Immutable

@Immutable
data class ModifyPlannedSkrinState(
    val currency: String,
    val transactionType: TransactionType,
    val startDate: LocalDateTime?,
    val intervalN: Int?,
    val intervalType: IntervalType?,
    val oneTime: Boolean,
    val initialTitle: String?,
    val description: String?,
    val categories: ImmutableList<Category>,
    val accounts: ImmutableList<Account>,
    val account: Account?,
    val category: Category?,
    val amount: Double,
    val categoryModalVisible: Boolean,
    val descriptionModalVisible: Boolean,
    val deleteTransactionModalVisible: Boolean,
    val categoryModalData: CategoryModalData?,
    val accountModalData: AccountModalData?,
    val recurringRuleModalData: RecurringRuleModalData?,
    val transactionTypeModalVisible: Boolean,
    val amountModalVisible: Boolean
)
