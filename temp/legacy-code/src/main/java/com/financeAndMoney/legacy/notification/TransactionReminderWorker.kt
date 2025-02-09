package com.financeAndMoney.legacy.notification

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.financeAndMoney.base.legacy.stringRes
import com.financeAndMoney.data.database.dao.read.TransactionDao
import com.financeAndMoney.domains.AppStarter
import com.financeAndMoney.base.legacy.SharedPrefs
import com.financeAndMoney.legacy.utils.atEndOfDay
import com.financeAndMoney.legacy.utils.dateNowUTC
import com.financeAndMoney.core.userInterface.R
import com.financeAndMoney.legacy.android.notification.MysaveNotificationChannel
import com.financeAndMoney.legacy.android.notification.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class TransactionReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val transactionDao: TransactionDao,
    private val notificationService: NotificationService,
    private val sharedPrefs: SharedPrefs,
    private val appStarter: AppStarter,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val MINIMUM_TRANSACTIONS_PER_DAY = 1
    }

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val transactionsToday = transactionDao.findAllBetween(
            startDate = dateNowUTC().atStartOfDay(),
            endDate = dateNowUTC().atEndOfDay()
        )

        val showNotifications = fetchShowNotifications()

        // Double check is needed because the user can switch off notifications in controls after it has been scheduled to show notifications for the next day
        if (transactionsToday.size < MINIMUM_TRANSACTIONS_PER_DAY && showNotifications) {
            // Have less than 1 two transfers today, remind them

            val notification = notificationService
                .defaultMysaveNotification(
                    channel = MysaveNotificationChannel.TRANSACTION_REMINDER,
                    priority = NotificationCompat.PRIORITY_HIGH
                )
                .setContentTitle("Mysave App")
                .setContentText(randomText())
                .setContentIntent(
                    PendingIntent.getActivity(
                        applicationContext,
                        1,
                        appStarter.getRootIntent(),
                        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_UPDATE_CURRENT
                            or PendingIntent.FLAG_IMMUTABLE
                    )
                )

            notificationService.showNotification(notification, 1)
        }

        return@withContext Result.success()
    }

    private fun randomText(): String =
        listOf(
            stringRes(R.string.notification_1),
            stringRes(R.string.notification_2),
            stringRes(R.string.notification_3),
        ).shuffled().first()

    private fun fetchShowNotifications(): Boolean =
        sharedPrefs.getBoolean(SharedPrefs.SHOW_NOTIFICATIONS, true)
}
