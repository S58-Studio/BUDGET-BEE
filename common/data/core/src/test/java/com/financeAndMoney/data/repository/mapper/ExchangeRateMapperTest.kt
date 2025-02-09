package com.financeAndMoney.data.repository.mapper

import com.financeAndMoney.data.database.entities.XchangeRateEntity
import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.model.primitive.PositiveDouble
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test

class ExchangeRateMapperTest {

    private lateinit var mapper: ExchangeRateMapper

    @Before
    fun setup() {
        mapper = ExchangeRateMapper()
    }

    @Test
    fun `maps domain to entity`() {
        // given
        val mapper = ExchangeRateMapper()
        val exchangeRate =
            com.financeAndMoney.data.model.ExchangeRate(
                baseCurrency = AssetCode.unsafe("USD"),
                currency = AssetCode.unsafe("AAVE"),
                rate = PositiveDouble.unsafe(0.000943049049897979),
                manualOverride = false,
            )

        // when
        val result = with(mapper) { exchangeRate.toEntity() }

        // then
        result shouldBe
                XchangeRateEntity(
                    baseCurrency = "USD",
                    currency = "AAVE",
                    rate = 0.000943049049897979,
                    manualOverride = false,
                )
    }

    @Test
    fun `maps entity to domain`() {
        // given
        val xchangeRateEntity =
            XchangeRateEntity(
                baseCurrency = "USD",
                currency = "AAVE",
                rate = 0.000943049049897979,
                manualOverride = false,
            )

        // when
        val result = with(mapper) { xchangeRateEntity.toDomain() }

        // then
        result.getOrNull() shouldBe com.financeAndMoney.data.model.ExchangeRate(
            baseCurrency = AssetCode.unsafe("USD"),
            currency = AssetCode.unsafe("AAVE"),
            rate = PositiveDouble.unsafe(0.000943049049897979),
            manualOverride = false,
        )
    }
}
