package com.oneSaver.data.backingUp

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oneSaver.base.TestDispatchersProvider
import com.oneSaver.base.di.KotlinxSerializationModule
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.data.DataObserver
import com.oneSaver.data.database.MylonRoomDatabase
import com.oneSaver.data.file.FileSystem
import com.oneSaver.data.repository.AccountRepository
import com.oneSaver.data.repository.CurrencyRepository
import com.oneSaver.data.repository.fake.fakeRepositoryMemoFactory
import com.oneSaver.data.repository.mapper.AccountMapper
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BackingUpDataUseCaseAndroidTest {

    private lateinit var db: MylonRoomDatabase
    private lateinit var useCase: BackingUpDataUseCase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MylonRoomDatabase::class.java).build()
        val appContext = InstrumentationRegistry.getInstrumentation().context
        val accountMapper = AccountMapper(
            currencyRepository = CurrencyRepository(
                settingsDao = db.settingsDao,
                writeSettingsDao = db.writeSettingsDao,
                dispatchersProvider = TestDispatchersProvider,
            )
        )
        useCase = BackingUpDataUseCase(
            accountDao = db.accountDao,
            budgetDao = db.budgetDao,
            categoryDao = db.categoryDao,
            loanRecordDao = db.loanRecordDao,
            loanDao = db.loanDao,
            plannedPaymentRuleDao = db.plannedPaymentRuleDao,
            settingsDao = db.settingsDao,
            transactionDao = db.transactionDao,
            transactionWriter = db.writeTransactionDao,
            sharedPrefs = SharedPrefs(appContext),
            accountRepository = AccountRepository(
                accountDao = db.accountDao,
                writeAccountDao = db.writeAccountDao,
                mapper = accountMapper,
                dispatchersProvider = TestDispatchersProvider,
                memoFactory = fakeRepositoryMemoFactory(),
            ),
            accountMapper = accountMapper,
            categoryWriter = db.writeCategoryDao,
            settingsWriter = db.writeSettingsDao,
            budgetWriter = db.writeBudgetDao,
            loanWriter = db.writeLoanDao,
            loanRecordWriter = db.writeLoanRecordDao,
            plannedPaymentRuleWriter = db.writePlannedPaymentRuleDao,
            context = appContext,
            json = KotlinxSerializationModule.provideJson(),
            dispatchersProvider = TestDispatchersProvider,
            fileSystem = FileSystem(appContext),
            dataObserver = DataObserver(),
        )
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun backup450_150() = runBlocking {
        backupTestCase("450-150")
    }

    private suspend fun backupTestCase(version: String) {
        importBackupZipTestCase(version)
        importBackupJsonTestCase(version)

        // close and re-open the db to ensure fresh data
        closeDb()
        createDb()
        exportsAndImportsTestCase(version)
    }

    private suspend fun importBackupZipTestCase(version: String) {
        // given
        val backupUri = copyTestResourceToInternalStorage("backups/$version.zip")

        // when
        val res = useCase.importBackupFile(backupUri, onProgress = {})

        // then
        res.shouldBeSuccessful()
    }

    private suspend fun importBackupJsonTestCase(version: String) {
        // given
        val backupUri = copyTestResourceToInternalStorage("backups/$version.json")

        // when
        val res = useCase.importBackupFile(backupUri, onProgress = {})

        // then
        res.shouldBeSuccessful()
    }

    private suspend fun exportsAndImportsTestCase(version: String) {
        // given
        val backupUri = copyTestResourceToInternalStorage("backups/$version.zip")
        // preload data
        useCase.importBackupFile(backupUri, onProgress = {}).shouldBeSuccessful()
        val exportedFileUri = tempAndroidFile("exported", ".zip").toUri()

        // then
        useCase.exportToFile(exportedFileUri)
        val reImportRes = useCase.importBackupFile(backupUri, onProgress = {})

        // then
        reImportRes.shouldBeSuccessful()
    }

    private fun copyTestResourceToInternalStorage(resPath: String): Uri {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val assetManager = context.assets
        val inputStream = assetManager.open(resPath)
        val outputFile = tempAndroidFile("temp-backingUp", resPath.split(".").last())
        outputFile.outputStream().use { fileOut ->
            fileOut.write(inputStream.readBytes())
        }
        return Uri.fromFile(outputFile)
    }

    private fun tempAndroidFile(prefix: String, suffix: String): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return File.createTempFile(prefix, suffix, context.filesDir)
    }

    private fun ImportingResults.shouldBeSuccessful() {
        failedRows.shouldBeEmpty()
        categoriesImported shouldBeGreaterThan 0
        accountsImported shouldBeGreaterThan 0
        transactionsImported shouldBeGreaterThan 0
    }
}