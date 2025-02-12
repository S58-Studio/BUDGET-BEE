package com.oneSaver.data.repository

import arrow.core.Either
import arrow.core.left
import com.oneSaver.base.TestDispatchersProvider
import com.oneSaver.data.database.dao.read.ExchangeRatesDao
import com.oneSaver.data.database.dao.write.WriteExchangeRatesDao
import com.oneSaver.data.model.ExchangeRate
import com.oneSaver.data.remote.RemoteExchangeRatesDataSource
import com.oneSaver.data.remote.responses.ExchangeRatesResponse
import com.oneSaver.data.repository.mapper.ExchangeRateMapper
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ExchangeRatesRepositoryTest {
    private val mapper = mockk<ExchangeRateMapper>()
    private val exchangeRatesDao = mockk<ExchangeRatesDao>()
    private val writeExchangeRatesDao = mockk<WriteExchangeRatesDao>()
    private val remoteExchangeRatesDataSource = mockk<RemoteExchangeRatesDataSource>()

    private lateinit var repository: ExchangeRatesRepository

    @Before
    fun setup() {
        repository = ExchangeRatesRepository(
            mapper = mapper,
            exchangeRatesDao = exchangeRatesDao,
            writeExchangeRatesDao = writeExchangeRatesDao,
            remoteExchangeRatesDataSource = remoteExchangeRatesDataSource,
            dispatchers = TestDispatchersProvider,
        )
    }

    @Test
    fun `fetchExchangeRates - successful network responses`() = runTest {
        // given
        val mockResponse = ExchangeRatesResponse(
            date = "",
            rates = emptyMap(),
        )
        val mockRates = mockk<List<ExchangeRate>>()
        with(mapper) {
            every { mockResponse.toDomain() } returns Either.Right(mockRates)
        }
        coEvery {
            remoteExchangeRatesDataSource.fetchEurExchangeRates()
        } returns Either.Right(mockResponse)

        // when
        val result = repository.fetchEurExchangeRates()

        // then
        result.shouldBeRight() shouldBe mockRates
    }

    @Test
    fun `fetchExchangeRates - unsuccessful network responses`() = runTest {
        // given
        val errResponse = "Network Error"
        coEvery {
            remoteExchangeRatesDataSource.fetchEurExchangeRates()
        } returns errResponse.left()

        // when
        val result = repository.fetchEurExchangeRates()

        // then
        result.shouldBeLeft() shouldBe errResponse
    }
}
