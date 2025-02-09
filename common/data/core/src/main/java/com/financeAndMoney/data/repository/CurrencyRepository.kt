package com.financeAndMoney.data.repository

import android.icu.util.Currency
import com.financeAndMoney.base.legacy.Theme
import com.financeAndMoney.base.threading.DispatchersProvider
import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.data.database.dao.write.WriteSettingsDao
import com.financeAndMoney.data.database.entities.SettingsEntity
import com.financeAndMoney.data.model.primitive.AssetCode
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val settingsDao: SettingsDao,
    private val writeSettingsDao: WriteSettingsDao,
    private val dispatchersProvider: DispatchersProvider,
) {
    companion object {
        const val FALLBACK_DEFAULT_CURRENCY = "USD"
    }

    private var baseCurrencyMemo: AssetCode? = null

    suspend fun getBaseCurrency(): AssetCode = withContext(dispatchersProvider.io) {
        val baseCurrency = baseCurrencyMemo
        if (baseCurrency != null) return@withContext baseCurrency

        val currencyCode = settingsDao.findFirstOrNull()?.currency
            ?: getDefaultFIATCurrency()?.currencyCode
        currencyCode?.let(AssetCode::from)?.getOrNull()
            ?: AssetCode.unsafe(FALLBACK_DEFAULT_CURRENCY)
    }

    private fun getDefaultFIATCurrency(): Currency? {
        return Currency.getInstance(Locale.getDefault())
    }

    suspend fun setBaseBaseCurrency(newCurrency: AssetCode) {
        withContext(dispatchersProvider.io) {
            val currentEntity = settingsDao.findFirstOrNull()
                ?: SettingsEntity(
                    theme = Theme.AUTO,
                    currency = FALLBACK_DEFAULT_CURRENCY,
                    bufferAmount = 0.0,
                    name = "",
                    isSynced = true,
                    isDeleted = false,
                    id = UUID.randomUUID()
                )
            baseCurrencyMemo = newCurrency
            writeSettingsDao.save(
                currentEntity.copy(
                    currency = newCurrency.code
                )
            )
        }
    }
}