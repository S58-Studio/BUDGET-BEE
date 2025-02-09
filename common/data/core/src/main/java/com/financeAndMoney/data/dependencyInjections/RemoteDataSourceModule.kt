package com.financeAndMoney.data.dependencyInjections

import com.financeAndMoney.data.remote.RemoteExchangeRatesDataSource
import com.financeAndMoney.data.remote.implementation.RemoteExchangeRatesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {
    @Binds
    abstract fun bindExchangeRatesDataSource(
        datasource: RemoteExchangeRatesDataSourceImpl
    ): RemoteExchangeRatesDataSource
}
