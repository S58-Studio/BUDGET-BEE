package com.financeAndMoney.data.database.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.financeAndMoney.data.database.entities.XchangeRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRatesDao {
    @Query("SELECT * FROM exchange_rates")
    fun findAll(): Flow<List<XchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates WHERE manualOverride = 1")
    suspend fun findAllManuallyOverridden(): List<XchangeRateEntity>

    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): XchangeRateEntity?
}
