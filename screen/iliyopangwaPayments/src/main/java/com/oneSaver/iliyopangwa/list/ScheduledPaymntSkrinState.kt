package com.oneSaver.iliyopangwa.list

import com.oneSaver.data.model.Category
import com.oneSaver.legacy.datamodel.Account
import com.oneSaver.legacy.datamodel.PlannedPaymentRule
import kotlinx.collections.immutable.ImmutableList
import javax.annotation.concurrent.Immutable

@Immutable
data class ScheduledPaymntSkrinState(
    val currency: String,
    val categories: ImmutableList<Category>,
    val accounts: ImmutableList<Account>,
    val oneTimePlannedPayment: ImmutableList<PlannedPaymentRule>,
    val oneTimeIncome: Double,
    val oneTimeExpenses: Double,
    val recurringPlannedPayment: ImmutableList<PlannedPaymentRule>,
    val recurringIncome: Double,
    val recurringExpenses: Double,
    val isOneTimePaymentsExpanded: Boolean,
    val isRecurringPaymentsExpanded: Boolean
)
