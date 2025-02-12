package com.oneSaver.allStatus

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here.
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: " + remoteMessage.data)
            sendNotification(remoteMessage.notification?.body)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
    }

    private fun sendNotification(messageBody: String?) {
        val notificationBuilder = NotificationCompat.Builder(this, "Mysave_Notifications_Channel")
            .setSmallIcon(R.mipmap.ic_launcher_round) // Replace with your app icon
            .setContentTitle("Mysave Notification")
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for pop-up
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        // Check if POST_NOTIFICATIONS permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("FCM", "Notification permission not granted. Cannot send notification.")
            return // Return if the permission is not granted
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        // Send token to your server
    }
}
