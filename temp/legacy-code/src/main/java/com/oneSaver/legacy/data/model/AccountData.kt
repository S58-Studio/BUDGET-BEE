package com.oneSaver.legacy.data.model

import androidx.compose.runtime.Immutable
import com.oneSaver.data.model.Account
import com.oneSaver.allStatus.domain.data.Reorderable

@Immutable
data class AccountData(
    val account: Account,
    val balance: Double,
    val balanceBaseCurrency: Double?,
    val monthlyExpenses: Double,
    val monthlyIncome: Double
) : Reorderable {
    override fun getItemOrderNum() = account.orderNum

    override fun withNewOrderNum(newOrderNum: Double) = this.copy(
        account = account.copy(
            orderNum = newOrderNum
        )
    )
}
