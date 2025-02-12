package com.oneSaver.legacy

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.data.DataObserver
import com.oneSaver.data.DataWriteEvent
import com.oneSaver.data.database.dao.read.UserDao
import com.oneSaver.data.database.dao.write.WriteBudgetDao
import com.oneSaver.data.database.dao.write.WriteLoanDao
import com.oneSaver.data.database.dao.write.WriteLoanRecordDao
import com.oneSaver.data.database.dao.write.WritePlannedPaymentRuleDao
import com.oneSaver.data.database.dao.write.WriteSettingsDao
import com.oneSaver.data.repository.AccountRepository
import com.oneSaver.data.repository.CategoryRepository
import com.oneSaver.data.repository.ExchangeRatesRepository
import com.oneSaver.data.repository.TagRepository
import com.oneSaver.data.repository.TransactionRepository
import com.oneSaver.legacy.utils.ioThread
import com.oneSaver.navigation.MainSkreen
import com.oneSaver.navigation.Navigation
import com.oneSaver.navigation.OnboardingScreen
import javax.inject.Inject

@Deprecated("Migrate to an UseCase in the domain layer.")
class LogoutLogic @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val navigation: Navigation,
    private val dataObserver: DataObserver,
    private val dataStore: DataStore<Preferences>,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val userDao: UserDao,
    private val writeSettingsDao: WriteSettingsDao,
    private val writePlannedPaymentRuleDao: WritePlannedPaymentRuleDao,
    private val writeBudgetDao: WriteBudgetDao,
    private val writeLoanDao: WriteLoanDao,
    private val writeLoanRecordDao: WriteLoanRecordDao,
    private val exchangeRatesRepository: ExchangeRatesRepository
) {
    suspend fun logout() {
        ioThread {
            deleteAllData()
            dataStore.edit {
                it.clear()
            }
            sharedPrefs.removeAll()
        }

        dataObserver.post(DataWriteEvent.AllDataChange)
        navigation.resetBackStack()
        navigation.navigateTo(OnboardingScreen)
    }

    private suspend fun deleteAllData() {
        accountRepository.deleteAll()
        transactionRepository.deleteAll()
        categoryRepository.deleteAll()
        tagRepository.deleteAll()
        writeSettingsDao.deleteAll()
        writePlannedPaymentRuleDao.deleteAll()
        userDao.deleteAll()
        writeBudgetDao.deleteAll()
        writeLoanDao.deleteAll()
        writeLoanRecordDao.deleteAll()
        exchangeRatesRepository.deleteAll()
    }

    suspend fun cloudLogout() {
        navigation.navigateTo(MainSkreen)
    }
}
