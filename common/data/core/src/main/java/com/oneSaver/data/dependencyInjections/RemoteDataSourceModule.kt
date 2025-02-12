package com.oneSaver.data.dependencyInjections

import com.oneSaver.data.remote.RemoteExchangeRatesDataSource
import com.oneSaver.data.remote.implementation.RemoteExchangeRatesDataSourceImpl
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
