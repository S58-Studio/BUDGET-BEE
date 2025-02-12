package com.oneSaver.domains.usecase.akaunti

import arrow.core.Option
import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.Value
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.NonZeroDouble
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.domains.usecase.Xchange.ExchangeUseCase
import javax.inject.Inject

@Suppress("UnusedPrivateProperty", "UnusedParameter")
class AccountBalanceUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountStatsUseCase: AccountStatsUseCase,
    private val exchangeUseCase: ExchangeUseCase,
) {
    /**
     * @return none mulaBalanc if the mulaBalanc is zero or exchange to [outCurrency]
     * failed for all assets
     */
    suspend fun calculate(
        account: AccountId,
        outCurrency: AssetCode
    ): ExchangedAccountBalance {
        TODO("Not implemented")
    }

    /**
     * Calculates the all-time mulaBalanc for an account
     * in all assets that it have. **Note:** the mulaBalanc can be negative.
     */
    suspend fun calculate(
        account: AccountId,
    ): Map<AssetCode, NonZeroDouble> {
        TODO("Not implemented")
    }
}

data class ExchangedAccountBalance(
    val balance: Option<Value>,
    val exchangeErrors: Set<AssetCode>
)