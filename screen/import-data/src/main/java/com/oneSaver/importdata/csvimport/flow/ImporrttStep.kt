package com.oneSaver.importdata.csvimport.flow

import androidx.compose.runtime.Composable
import com.oneSaver.importdata.csvimport.flow.masharti.FinancistoSteps
import com.oneSaver.importdata.csvimport.flow.masharti.MySaveSteps
import com.oneSaver.importdata.csvimport.flow.masharti.KTWMoneyManagerSteps
import com.oneSaver.importdata.csvimport.flow.masharti.MonefySteps
import com.oneSaver.importdata.csvimport.flow.masharti.MoneyManagerPraseSteps
import com.oneSaver.importdata.csvimport.flow.masharti.OneMoneySteps
import com.oneSaver.importdata.csvimport.flow.masharti.SpendeeSteps
import com.oneSaver.importdata.csvimport.flow.masharti.WalletByBudgetBakersSteps
import com.oneSaver.legacy.domain.deprecated.logic.csv.model.ImportType

@Composable
fun ImportType.ImportSteps(
    onUploadClick: () -> Unit
) {
    when (this) {
        ImportType.MYSAVE -> {
            MySaveSteps(
                onUploadClick = onUploadClick
            )
        }

        ImportType.MONEY_MANAGER -> {
            MoneyManagerPraseSteps(
                onUploadClick = onUploadClick
            )
        }

        ImportType.WALLET_BY_BUDGET_BAKERS -> {
            WalletByBudgetBakersSteps(
                onUploadClick = onUploadClick
            )
        }

        ImportType.SPENDEE -> SpendeeSteps(
            onUploadClick = onUploadClick
        )

        ImportType.MONEFY -> MonefySteps(
            onUploadClick = onUploadClick
        )

        ImportType.ONE_MONEY -> OneMoneySteps(
            onUploadClick = onUploadClick
        )


        ImportType.KTW_MONEY_MANAGER -> KTWMoneyManagerSteps(
            onUploadClick = onUploadClick
        )

        ImportType.FINANCISTO -> FinancistoSteps(
            onUploadClick = onUploadClick
        )
    }
}
