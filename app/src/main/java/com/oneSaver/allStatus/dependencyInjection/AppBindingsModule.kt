package com.oneSaver.allStatus.dependencyInjection

import com.oneSaver.domains.AppStarter
import com.oneSaver.allStatus.MylonAppStarter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {
    @Binds
    abstract fun appStarter(appStarter: MylonAppStarter): AppStarter
}
