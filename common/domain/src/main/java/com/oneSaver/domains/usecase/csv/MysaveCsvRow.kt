package com.oneSaver.domains.usecase.csv

import com.oneSaver.base.model.TransactionType
import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.CategoryId
import com.oneSaver.data.model.TransactionId
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.NonNegativeDouble
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.model.primitive.PositiveDouble
import java.time.Instant

// TODO: Fix Ivy Explicit detekt false-positives
@SuppressWarnings("DataClassTypedIDs")
data class MysaveCsvRow(
    val date: Instant?,
    val title: NotBlankTrimmedString?,
    val category: CategoryId?,
    val account: AccountId,
    val amount: NonNegativeDouble,
    val currency: AssetCode,
    val type: TransactionType,
    val transferAmount: PositiveDouble?,
    val transferCurrency: AssetCode?,
    val toAccountId: AccountId?,
    val receiveAmount: PositiveDouble?,
    val receiveCurrency: AssetCode?,
    val description: NotBlankTrimmedString?,
    val dueData: Instant?,
    val id: TransactionId
) {
    companion object {
        val Columns = listOf(
            "Date",
            "Title",
            "Category",
            "Account",
            "Amount",
            "Currency",
            "Type",
            "Transfer Amount",
            "Transfer Currency",
            "To Account",
            "Receive Amount",
            "Receive Currency",
            "Description",
            "Due Date",
            "ID",
        )
    }
}