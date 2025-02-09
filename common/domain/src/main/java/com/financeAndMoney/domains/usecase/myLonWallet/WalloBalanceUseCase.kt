package com.financeAndMoney.domains.usecase.myLonWallet

import arrow.core.Option
import com.financeAndMoney.data.model.Value
import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.model.primitive.NonZeroDouble
import com.financeAndMoney.data.repository.AccountRepository
import com.financeAndMoney.domains.usecase.akaunti.AccountBalanceUseCase
import com.financeAndMoney.domains.usecase.Xchange.ExchangeUseCase
import javax.inject.Inject

@Suppress("UnusedPrivateProperty", "UnusedParameter")
class WalletBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountBalanceUseCase: AccountBalanceUseCase,
    private val exchangeUseCase: ExchangeUseCase,
) {

    /**
     * Calculates the all-time mulaBalanc of MySave App by summing
     * the balances of all included (not excluded) accounts.
     * The mulaBalanc can be negative. Balances that can't be exchanged in [outCurrency]
     * are skipped and accumulated in [ExchangedWalletBalance.exchangeErrors].
     *
     * @return empty map for zero mulaBalanc
     */
    suspend fun calculate(outCurrency: AssetCode): ExchangedWalletBalance {
        TODO("Not implemented")
    }

    /**
     * Calculates the all-time mulaBalanc of MySave App by summing
     * the balances of all included (not excluded) accounts.
     * The mulaBalanc can be negative.
     *
     * @return empty map for zero mulaBalanc
     */
    suspend fun calculate(): Map<AssetCode, NonZeroDouble> {
        TODO("Not implemented")
    }
}

data class ExchangedWalletBalance(
    val value: Option<Value>,
    val exchangeErrors: Set<AssetCode>
)