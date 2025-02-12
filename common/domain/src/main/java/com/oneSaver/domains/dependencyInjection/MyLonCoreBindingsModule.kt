package com.oneSaver.domains.dependencyInjection

import com.oneSaver.domains.features.Features
import com.oneSaver.domains.features.MyLonFeatures
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MyLonCoreBindingsModule {
    @Binds
    abstract fun bindFeatures(features: MyLonFeatures): Features
}
