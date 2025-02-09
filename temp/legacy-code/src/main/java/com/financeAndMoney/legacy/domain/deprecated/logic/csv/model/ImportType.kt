package com.financeAndMoney.legacy.domain.deprecated.logic.csv.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.financeAndMoney.design.l0_system.Green
import com.financeAndMoney.design.l0_system.Ivy
import com.financeAndMoney.design.l0_system.Red
import com.financeAndMoney.design.l0_system.Red3
import com.financeAndMoney.design.l0_system.RedLight
import com.financeAndMoney.design.l0_system.White
import com.financeAndMoney.design.l0_system.Yellow
import com.financeAndMoney.core.userInterface.R

@Immutable
enum class ImportType {
    MYSAVE,
    MONEY_MANAGER,
    WALLET_BY_BUDGET_BAKERS,
    SPENDEE,
    MONEFY,
    ONE_MONEY,
    KTW_MONEY_MANAGER,
    FINANCISTO;

    fun color(): Color = when (this) {
        MYSAVE -> Ivy
        MONEY_MANAGER -> Red
        WALLET_BY_BUDGET_BAKERS -> Green
        SPENDEE -> RedLight
        MONEFY -> Green
        ONE_MONEY -> Red3
        KTW_MONEY_MANAGER -> Yellow
        FINANCISTO -> White
    }

    fun appId(): String = when (this) {
        MYSAVE -> "com.financeAndMoney.expenseAndBudgetPlanner"
        MONEY_MANAGER -> "com.realbyteapps.moneymanagerfree"
        WALLET_BY_BUDGET_BAKERS -> "com.droid4you.application.expenseAndBudgetPlanner"
        SPENDEE -> "com.cleevio.spendee"
        MONEFY -> "com.monefy.app.lite"
        ONE_MONEY -> "org.pixelrush.moneyiq"
        KTW_MONEY_MANAGER -> "com.ktwapps.walletmanager"
        FINANCISTO -> "ru.orangesoftware.financisto"
    }

    @DrawableRes
    fun logo(): Int = when (this) {
        MYSAVE -> R.drawable.money_manager_logo
        MONEY_MANAGER -> R.drawable.money_manager_logo
        WALLET_BY_BUDGET_BAKERS -> R.drawable.wallet_byy_budgetbakers_logo
        SPENDEE -> R.drawable.spendee_app_logo
        MONEFY -> R.drawable.monefyy_logo
        ONE_MONEY -> R.drawable.onemoney_logo
        KTW_MONEY_MANAGER -> R.drawable.ktw_finance_manager_logo
        FINANCISTO -> R.drawable.financ_isto_logo
    }

    fun listName(): String = when (this) {
        MONEFY -> "Monefy"
        ONE_MONEY -> "1Money"
        KTW_MONEY_MANAGER -> "Money Manager (KTW)"
        FINANCISTO -> "Financisto"
        MYSAVE -> "Mysave Wallet"
        MONEY_MANAGER -> "Money Manager"
        WALLET_BY_BUDGET_BAKERS -> "Wallet by BudgetBakers"
        SPENDEE -> "Spendee"
    }

    fun appName(): String = when (this) {
        MYSAVE -> "MySave App"
        else -> listName()
    }
}
