package com.financeAndMoney.expenseAndBudgetPlanner.domain.action.wallet

import arrow.core.toOption
import com.financeAndMoney.data.model.Account
import com.financeAndMoney.data.model.AccountId
import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.model.primitive.ColorInt
import com.financeAndMoney.data.model.primitive.IconAsset
import com.financeAndMoney.data.model.primitive.NotBlankTrimmedString
import com.financeAndMoney.frp.action.FPAction
import com.financeAndMoney.frp.action.thenFilter
import com.financeAndMoney.frp.action.thenMap
import com.financeAndMoney.frp.action.thenSum
import com.financeAndMoney.frp.fixUnit
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.AccountsAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.account.CalcAccBalanceAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.action.exchange.ExchangeAct
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.data.ClosedTimeRange
import com.financeAndMoney.expenseAndBudgetPlanner.domain.pure.exchange.ExchangeData
import java.math.BigDecimal
import javax.inject.Inject

class CalcWalletBalanceAct @Inject constructor(
    private val accountsAct: AccountsAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val exchangeAct: ExchangeAct,
) : FPAction<CalcWalletBalanceAct.Input, BigDecimal>() {

    override suspend fun Input.compose(): suspend () -> BigDecimal = recipe().fixUnit()

    private suspend fun Input.recipe(): suspend (Unit) -> BigDecimal =
        accountsAct thenFilter {
            withExcluded || it.includeInBalance
        } thenMap { account ->
            calcAccBalanceAct(
                CalcAccBalanceAct.Input(
                    account = Account(
                        id = AccountId(account.id),
                        name = NotBlankTrimmedString.from(account.name).getOrNull()
                            ?: error("account name cannot be blank"),
                        asset = AssetCode.from(account.currency ?: baseCurrency).getOrNull()
                            ?: error("account currency cannot be blank"),
                        color = ColorInt(account.color),
                        icon = account.icon?.let { IconAsset.from(it).getOrNull() },
                        includeInBalance = account.includeInBalance,
                        orderNum = account.orderNum,
                    ),
                    range = range
                )
            )
        } thenMap {
            exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency,
                        fromCurrency = (it.account.asset.code).toOption(),
                        toCurrency = balanceCurrency
                    ),
                    amount = it.balance
                )
            )
        } thenSum {
            it.orNull() ?: BigDecimal.ZERO
        }

    data class Input(
        val baseCurrency: String,
        val balanceCurrency: String = baseCurrency,
        val range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
        val withExcluded: Boolean = false
    )
}
