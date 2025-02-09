package com.financeAndMoney.domains.usecase

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.financeAndMoney.base.TestDispatchersProvider
import com.financeAndMoney.base.di.KotlinxSerializationModule
import com.financeAndMoney.data.database.MylonRoomDatabase
import com.financeAndMoney.data.dependencyInjections.KtorClientModule
import com.financeAndMoney.data.model.primitive.AssetCode
import com.financeAndMoney.data.remote.implementation.RemoteExchangeRatesDataSourceImpl
import com.financeAndMoney.data.repository.ExchangeRatesRepository
import com.financeAndMoney.data.repository.mapper.ExchangeRateMapper
import com.financeAndMoney.domains.usecase.Xchange.SyncXchangeRatesUseCase
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncXchangeRatesUseCaseTest {
    private lateinit var useCase: SyncXchangeRatesUseCase
    private lateinit var repository: ExchangeRatesRepository
    private lateinit var db: MylonRoomDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MylonRoomDatabase::class.java).build()

        repository = ExchangeRatesRepository(
            exchangeRatesDao = db.exchangeRatesDao,
            writeExchangeRatesDao = db.writeExchangeRatesDao,
            mapper = ExchangeRateMapper(),
            remoteExchangeRatesDataSource = RemoteExchangeRatesDataSourceImpl(
                ktorClient = {
                    KtorClientModule.provideKtorClient(KotlinxSerializationModule.provideJson())
                },
            ),
            dispatchers = TestDispatchersProvider,
        )

        useCase = SyncXchangeRatesUseCase(repository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun syncsExchangeRates(): Unit = runBlocking {
        // given
        val exchangeRatesDao = db.exchangeRatesDao

        // when
        val res = useCase.sync(AssetCode.USD)

        // then
        res.shouldBeRight()
        val savedRates = exchangeRatesDao.findAll().first()
        savedRates.shouldNotBeEmpty()
        println("Saved ${savedRates.size} exchange rates")
        val eurRate = savedRates.firstOrNull { it.currency == "EUR" }?.rate
        eurRate.shouldNotBeNull() shouldBeGreaterThan 0.0
    }
}
