package com.oneSaver.legacy.notification

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oneSaver.base.legacy.stringRes
import com.oneSaver.data.database.dao.read.TransactionDao
import com.oneSaver.domains.AppStarter
import com.oneSaver.base.legacy.SharedPrefs
import com.oneSaver.base.time.TimeConverter
import com.oneSaver.base.time.TimeProvider
import com.oneSaver.legacy.utils.atEndOfDay
import com.oneSaver.legacy.utils.dateNowUTC
import com.oneSaver.core.userInterface.R
import com.oneSaver.legacy.android.notification.MysaveNotificationChannel
import com.oneSaver.legacy.android.notification.NotificationService
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
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val MINIMUM_TRANSACTIONS_PER_DAY = 1
    }

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val transactionsToday = with(timeConverter) {
            transactionDao.findAllBetween(
                startDate = timeProvider.localDateNow().atStartOfDay().toUTC(),
                endDate = timeProvider.localDateNow().atEndOfDay().toUTC(),
            )
        }

        val showNotifications = fetchShowNotifications()

        // Double check is needed because the user can switch off notifications in settings after it has been scheduled to show notifications for the next day
        if (transactionsToday.size < MINIMUM_TRANSACTIONS_PER_DAY && showNotifications) {
            // Have less than 1 two transactions today, remind them

            val notification = notificationService
                .defaultMysaveNotification(
                    channel = MysaveNotificationChannel.TRANSACTION_REMINDER,
                    priority = NotificationCompat.PRIORITY_HIGH
                )
                .setContentTitle("Ivy Wallet")
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
