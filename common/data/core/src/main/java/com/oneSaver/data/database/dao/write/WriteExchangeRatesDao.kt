package com.oneSaver.data.database.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.oneSaver.data.database.entities.XchangeRateEntity

@Dao
interface WriteExchangeRatesDao {
    @Upsert
    suspend fun save(value: XchangeRateEntity)

    @Upsert
    suspend fun saveMany(value: List<XchangeRateEntity>)

    @Query("DELETE FROM exchange_rates WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun deleteByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    )

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteAll()
}
