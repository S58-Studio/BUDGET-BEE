package com.oneSaver.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.oneSaver.data.database.dao.read.AccountDao
import com.oneSaver.data.database.dao.read.BudgetDao
import com.oneSaver.data.database.dao.read.CategoryDao
import com.oneSaver.data.database.dao.read.ExchangeRatesDao
import com.oneSaver.data.database.dao.read.LoanDao
import com.oneSaver.data.database.dao.read.LoanRecordDao
import com.oneSaver.data.database.dao.read.PlannedPaymentRuleDao
import com.oneSaver.data.database.dao.read.SettingsDao
import com.oneSaver.data.database.dao.read.TagDao
import com.oneSaver.data.database.dao.read.TagAssociationDao
import com.oneSaver.data.database.dao.read.TransactionDao
import com.oneSaver.data.database.dao.read.UserDao
import com.oneSaver.data.database.dao.write.WriteAccountDao
import com.oneSaver.data.database.dao.write.WriteBudgetDao
import com.oneSaver.data.database.dao.write.WriteCategoryDao
import com.oneSaver.data.database.dao.write.WriteExchangeRatesDao
import com.oneSaver.data.database.dao.write.WriteLoanDao
import com.oneSaver.data.database.dao.write.WriteLoanRecordDao
import com.oneSaver.data.database.dao.write.WritePlannedPaymentRuleDao
import com.oneSaver.data.database.dao.write.WriteSettingsDao
import com.oneSaver.data.database.dao.write.WriteTagDao
import com.oneSaver.data.database.dao.write.WriteTagAssociationDao
import com.oneSaver.data.database.dao.write.WriteTransactionDao
import com.oneSaver.data.database.entities.AccountEntity
import com.oneSaver.data.database.entities.BudgetEntity
import com.oneSaver.data.database.entities.CategoryEntity
import com.oneSaver.data.database.entities.XchangeRateEntity
import com.oneSaver.data.database.entities.LoanEntity
import com.oneSaver.data.database.entities.LoanRecordEntity
import com.oneSaver.data.database.entities.ScheduledPaymentRuleEntity
import com.oneSaver.data.database.entities.SettingsEntity
import com.oneSaver.data.database.entities.TagEntity
import com.oneSaver.data.database.entities.TagAssociationEntity
import com.oneSaver.data.database.entities.TransactionEntity
import com.oneSaver.data.database.entities.UserEntity
import com.oneSaver.data.database.migrations.Migration123to124_LoanIncludeDateTime
import com.oneSaver.data.database.migrations.Migration124to125_LoanEditDateTime
import com.oneSaver.data.database.migrations.Migration126to127_LoanRecordType
import com.oneSaver.data.database.migrations.Migration127to128_PaidForDateRecord
import com.oneSaver.data.database.migrations.Migration128to129_DeleteIsDeleted
import com.oneSaver.data.database.migrations.Migration105to106_TrnRecurringRules
import com.oneSaver.data.database.migrations.Migration106to107_Wishlist
import com.oneSaver.data.database.migrations.Migration107to108_Sync
import com.oneSaver.data.database.migrations.Migration108to109_Users
import com.oneSaver.data.database.migrations.Migration109to110_PlannedPayments
import com.oneSaver.data.database.migrations.Migration110to111_PlannedPaymentRule
import com.oneSaver.data.database.migrations.Migration111to112_User_testUser
import com.oneSaver.data.database.migrations.Migration112to113_ExchangeRates
import com.oneSaver.data.database.migrations.Migration113to114_Multi_Currency
import com.oneSaver.data.database.migrations.Migration114to115_Category_Account_Icons
import com.oneSaver.data.database.migrations.Migration115to116_Account_Include_In_Balance
import com.oneSaver.data.database.migrations.Migration116to117_SalteEdgeIntgration
import com.oneSaver.data.database.migrations.Migration117to118_Budgets
import com.oneSaver.data.database.migrations.Migration118to119_Loans
import com.oneSaver.data.database.migrations.Migration119to120_LoanTransactions
import com.oneSaver.data.database.migrations.Migration120to121_DropWishlistItem
import com.oneSaver.data.database.migrations.Migration122to123_ExchangeRates
import com.oneSaver.data.database.migrations.Migration125to126_Tags
import com.oneSaver.data.database.migrations.Migration129to130_LoanIncludeNote

@Database(
    entities = [
        AccountEntity::class, TransactionEntity::class, CategoryEntity::class,
        SettingsEntity::class, ScheduledPaymentRuleEntity::class,
        UserEntity::class, XchangeRateEntity::class, BudgetEntity::class,
        LoanEntity::class, LoanRecordEntity::class, TagEntity::class, TagAssociationEntity::class
    ],
    autoMigrations = [
        AutoMigration(
            from = 121,
            to = 122,
            spec = MylonRoomDatabase.DeleteSEMigration::class
        )
    ],
    version = 130,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class MylonRoomDatabase : RoomDatabase() {
    abstract val accountDao: AccountDao
    abstract val transactionDao: TransactionDao
    abstract val categoryDao: CategoryDao
    abstract val budgetDao: BudgetDao
    abstract val plannedPaymentRuleDao: PlannedPaymentRuleDao
    abstract val settingsDao: SettingsDao
    abstract val userDao: UserDao
    abstract val exchangeRatesDao: ExchangeRatesDao
    abstract val loanDao: LoanDao
    abstract val loanRecordDao: LoanRecordDao
    abstract val tagDao: TagDao
    abstract val tagAssociationDao: TagAssociationDao

    abstract val writeAccountDao: WriteAccountDao
    abstract val writeTransactionDao: WriteTransactionDao
    abstract val writeCategoryDao: WriteCategoryDao
    abstract val writeBudgetDao: WriteBudgetDao
    abstract val writePlannedPaymentRuleDao: WritePlannedPaymentRuleDao
    abstract val writeSettingsDao: WriteSettingsDao
    abstract val writeExchangeRatesDao: WriteExchangeRatesDao
    abstract val writeLoanDao: WriteLoanDao
    abstract val writeLoanRecordDao: WriteLoanRecordDao
    abstract val writeTagDao: WriteTagDao
    abstract val writeTagAssociationDao: WriteTagAssociationDao

    companion object {
        const val DB_NAME = "mylon.db"

        fun migrations() = arrayOf(
            Migration105to106_TrnRecurringRules(),
            Migration106to107_Wishlist(),
            Migration107to108_Sync(),
            Migration108to109_Users(),
            Migration109to110_PlannedPayments(),
            Migration110to111_PlannedPaymentRule(),
            Migration111to112_User_testUser(),
            Migration112to113_ExchangeRates(),
            Migration113to114_Multi_Currency(),
            Migration114to115_Category_Account_Icons(),
            Migration115to116_Account_Include_In_Balance(),
            Migration116to117_SalteEdgeIntgration(),
            Migration117to118_Budgets(),
            Migration118to119_Loans(),
            Migration119to120_LoanTransactions(),
            Migration120to121_DropWishlistItem(),
            Migration122to123_ExchangeRates(),
            Migration123to124_LoanIncludeDateTime(),
            Migration124to125_LoanEditDateTime(),
            Migration125to126_Tags(),
            Migration126to127_LoanRecordType(),
            Migration127to128_PaidForDateRecord(),
            Migration128to129_DeleteIsDeleted(),
            Migration129to130_LoanIncludeNote()
        )

        @Suppress("SpreadOperator")
        fun create(applicationContext: Context): MylonRoomDatabase {
            return Room
                .databaseBuilder(
                    applicationContext,
                    MylonRoomDatabase::class.java,
                    DB_NAME
                )
                .addMigrations(*migrations())
                .build()
        }
    }

    @DeleteColumn(tableName = "accounts", columnName = "seAccountId")
    @DeleteColumn(tableName = "transactions", columnName = "seTransactionId")
    @DeleteColumn(tableName = "transactions", columnName = "seAutoCategoryId")
    @DeleteColumn(tableName = "categories", columnName = "seCategoryName")
    class DeleteSEMigration : AutoMigrationSpec
}
