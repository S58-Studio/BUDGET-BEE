package com.oneSaver.base.di

import com.oneSaver.base.resource.AndroidResourceProvider
import com.oneSaver.base.resource.ResourceProvider
import com.oneSaver.base.threading.DispatchersProvider
import com.oneSaver.base.threading.IvyDispatchersProvider
import com.oneSaver.base.time.TimeConverter
import com.oneSaver.base.time.TimeProvider
import com.oneSaver.base.time.impl.DeviceTimeProvider
import com.oneSaver.base.time.impl.StandardTimeConverter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BaseHiltBindings {
    @Binds
    fun dispatchersProvider(impl: IvyDispatchersProvider): DispatchersProvider

    @Binds
    fun bindTimezoneProvider(impl: DeviceTimeProvider): TimeProvider

    @Binds
    fun bindTimeConverter(impl: StandardTimeConverter): TimeConverter

    @Binds
    fun resourceProvider(impl: AndroidResourceProvider): ResourceProvider
}