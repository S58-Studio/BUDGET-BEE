package com.oneSaver.legacy.domain.deprecated.logic

import androidx.compose.ui.graphics.toArgb
import arrow.core.raise.either
import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.data.model.Account
import com.oneSaver.data.model.AccountId
import com.oneSaver.data.model.primitive.AssetCode
import com.oneSaver.data.model.primitive.ColorInt
import com.oneSaver.data.model.primitive.IconAsset
import com.oneSaver.data.model.primitive.NotBlankTrimmedString
import com.oneSaver.data.repository.AccountRepository
import com.oneSaver.data.repository.CurrencyRepository
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.allStatus.domain.deprecated.logic.WalletAccountLogic
import com.oneSaver.allStatus.domain.deprecated.logic.model.CreateAccountData
import com.oneSaver.allStatus.domain.pure.util.nextOrderNum
import java.util.UUID
import javax.inject.Inject
import com.oneSaver.legacy.datamodel.Account as LegacyAccount

class AccountCreator @Inject constructor(
    private val accountLogic: WalletAccountLogic,
    private val accountDao: AccountDao,
    private val accountRepository: AccountRepository,
    private val currencyRepository: CurrencyRepository,
) {

    suspend fun createAccount(
        data: CreateAccountData,
        onRefreshUI: suspend () -> Unit
    ) {
        ioThread {
            val account = either {
                Account(
                    id = AccountId(value = UUID.randomUUID()),
                    name = NotBlankTrimmedString.from(data.name).bind(),
                    asset = AssetCode.from(data.currency).bind(),
                    color = ColorInt(data.color.toArgb()),
                    icon = data.icon?.let(IconAsset::from)?.getOrNull(),
                    includeInBalance = data.includeBalance,
                    orderNum = accountDao.findMaxOrderNum().nextOrderNum(),
                )
            }.getOrNull() ?: return@ioThread
            accountRepository.save(account)

            val legacyAccount = LegacyAccount(
                name = data.name,
                currency = data.currency,
                color = data.color.toArgb(),
                icon = data.icon,
                includeInBalance = data.includeBalance,
                orderNum = accountDao.findMaxOrderNum().nextOrderNum(),
                isSynced = false,
                id = account.id.value
            )
            accountLogic.adjustBalance(
                account = legacyAccount,
                actualBalance = 0.0,
                newBalance = data.balance
            )
        }

        onRefreshUI()
    }

    suspend fun editAccount(
        legacyAccount: LegacyAccount,
        newBalance: Double,
        onRefreshUI: suspend () -> Unit
    ) {
        val updatedLegacyAccount = legacyAccount.copy(
            isSynced = false
        )
        ioThread {
            val account = legacyAccount.toDomainAccount(currencyRepository).getOrNull()
                ?: return@ioThread
            accountRepository.save(account)

            accountLogic.adjustBalance(
                account = updatedLegacyAccount,
                actualBalance = accountLogic.calculateAccountBalance(updatedLegacyAccount),
                newBalance = newBalance
            )
        }

        onRefreshUI()
    }
}
