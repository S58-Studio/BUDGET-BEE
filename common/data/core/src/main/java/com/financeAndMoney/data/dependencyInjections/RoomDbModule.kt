package com.financeAndMoney.data.dependencyInjections

import android.content.Context
import com.financeAndMoney.data.database.MylonRoomDatabase
import com.financeAndMoney.data.database.dao.read.AccountDao
import com.financeAndMoney.data.database.dao.read.BudgetDao
import com.financeAndMoney.data.database.dao.read.CategoryDao
import com.financeAndMoney.data.database.dao.read.ExchangeRatesDao
import com.financeAndMoney.data.database.dao.read.LoanDao
import com.financeAndMoney.data.database.dao.read.LoanRecordDao
import com.financeAndMoney.data.database.dao.read.PlannedPaymentRuleDao
import com.financeAndMoney.data.database.dao.read.SettingsDao
import com.financeAndMoney.data.database.dao.read.TagAssociationDao
import com.financeAndMoney.data.database.dao.read.TagDao
import com.financeAndMoney.data.database.dao.read.TransactionDao
import com.financeAndMoney.data.database.dao.read.UserDao
import com.financeAndMoney.data.database.dao.write.WriteAccountDao
import com.financeAndMoney.data.database.dao.write.WriteBudgetDao
import com.financeAndMoney.data.database.dao.write.WriteCategoryDao
import com.financeAndMoney.data.database.dao.write.WriteExchangeRatesDao
import com.financeAndMoney.data.database.dao.write.WriteLoanDao
import com.financeAndMoney.data.database.dao.write.WriteLoanRecordDao
import com.financeAndMoney.data.database.dao.write.WritePlannedPaymentRuleDao
import com.financeAndMoney.data.database.dao.write.WriteSettingsDao
import com.financeAndMoney.data.database.dao.write.WriteTagAssociationDao
import com.financeAndMoney.data.database.dao.write.WriteTagDao
import com.financeAndMoney.data.database.dao.write.WriteTransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDbModule {

    @Provides
    @Singleton
    fun provideIvyRoomDatabase(
        @ApplicationContext appContext: Context,
    ): MylonRoomDatabase {
        return MylonRoomDatabase.create(
            applicationContext = appContext,
        )
    }

    @Provides
    fun provideUserDao(db: MylonRoomDatabase): UserDao {
        return db.userDao
    }

    @Provides
    fun provideAccountDao(db: MylonRoomDatabase): AccountDao {
        return db.accountDao
    }

    @Provides
    fun provideTransactionDao(db: MylonRoomDatabase): TransactionDao {
        return db.transactionDao
    }

    @Provides
    fun provideCategoryDao(db: MylonRoomDatabase): CategoryDao {
        return db.categoryDao
    }

    @Provides
    fun provideBudgetDao(db: MylonRoomDatabase): BudgetDao {
        return db.budgetDao
    }

    @Provides
    fun provideSettingsDao(db: MylonRoomDatabase): SettingsDao {
        return db.settingsDao
    }

    @Provides
    fun provideLoanDao(db: MylonRoomDatabase): LoanDao {
        return db.loanDao
    }

    @Provides
    fun provideLoanRecordDao(db: MylonRoomDatabase): LoanRecordDao {
        return db.loanRecordDao
    }

    @Provides
    fun providePlannedPaymentRuleDao(db: MylonRoomDatabase): PlannedPaymentRuleDao {
        return db.plannedPaymentRuleDao
    }

    @Provides
    fun provideTagDao(db: MylonRoomDatabase): TagDao {
        return db.tagDao
    }

    @Provides
    fun provideTagAssociationDao(db: MylonRoomDatabase): TagAssociationDao {
        return db.tagAssociationDao
    }

    @Provides
    fun provideExchangeRatesDao(
        roomDatabase: MylonRoomDatabase
    ): ExchangeRatesDao {
        return roomDatabase.exchangeRatesDao
    }

    @Provides
    fun provideWriteAccountDao(db: MylonRoomDatabase): WriteAccountDao {
        return db.writeAccountDao
    }

    @Provides
    fun provideWriteTransactionDao(db: MylonRoomDatabase): WriteTransactionDao {
        return db.writeTransactionDao
    }

    @Provides
    fun provideWriteCategoryDao(db: MylonRoomDatabase): WriteCategoryDao {
        return db.writeCategoryDao
    }

    @Provides
    fun provideWriteBudgetDao(db: MylonRoomDatabase): WriteBudgetDao {
        return db.writeBudgetDao
    }

    @Provides
    fun provideWriteSettingsDao(db: MylonRoomDatabase): WriteSettingsDao {
        return db.writeSettingsDao
    }

    @Provides
    fun provideWriteLoanDao(db: MylonRoomDatabase): WriteLoanDao {
        return db.writeLoanDao
    }

    @Provides
    fun provideWriteLoanRecordDao(db: MylonRoomDatabase): WriteLoanRecordDao {
        return db.writeLoanRecordDao
    }

    @Provides
    fun provideWritePlannedPaymentRuleDao(db: MylonRoomDatabase): WritePlannedPaymentRuleDao {
        return db.writePlannedPaymentRuleDao
    }

    @Provides
    fun provideWriteExchangeRatesDao(db: MylonRoomDatabase): WriteExchangeRatesDao {
        return db.writeExchangeRatesDao
    }

    @Provides
    fun provideWriteTagDao(db: MylonRoomDatabase): WriteTagDao {
        return db.writeTagDao
    }

    @Provides
    fun provideWriteTagAssociationDao(db: MylonRoomDatabase): WriteTagAssociationDao {
        return db.writeTagAssociationDao
    }
}
