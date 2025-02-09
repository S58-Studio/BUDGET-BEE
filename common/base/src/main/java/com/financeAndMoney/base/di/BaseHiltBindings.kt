package com.financeAndMoney.base.di

import com.financeAndMoney.base.threading.DispatchersProvider
import com.financeAndMoney.base.threading.IvyDispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BaseHiltBindings {
    @Binds
    abstract fun dispatchersProvider(impl: IvyDispatchersProvider): DispatchersProvider
}