package com.oneSaver.allStatus.domain.deprecated.logic.model

import androidx.compose.ui.graphics.Color
import com.oneSaver.base.model.LoanType
import com.oneSaver.legacy.datamodel.Account
import java.time.LocalDateTime

data class CreateLoanData(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Color,
    val icon: String?,
    val account: Account? = null,
    val createLoanTransaction: Boolean = false,
    val dateTime: LocalDateTime
)
