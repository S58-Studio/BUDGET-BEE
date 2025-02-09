package com.financeAndMoney.legacy.android.notification

import android.content.Context
import androidx.core.app.NotificationCompat

class MysaveNotification(
    context: Context,
    val mysaveChannel: MysaveNotificationChannel
) : NotificationCompat.Builder(context, mysaveChannel.channelId)
