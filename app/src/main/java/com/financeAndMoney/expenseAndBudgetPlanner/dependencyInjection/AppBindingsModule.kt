package com.financeAndMoney.expenseAndBudgetPlanner.dependencyInjection

import com.financeAndMoney.domains.AppStarter
import com.financeAndMoney.expenseAndBudgetPlanner.MylonAppStarter
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
