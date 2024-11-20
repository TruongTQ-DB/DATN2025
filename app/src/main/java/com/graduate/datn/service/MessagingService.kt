package com.graduate.datn.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.graduate.datn.MainActivity
import com.graduate.datn.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class  MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationNumber = generateRandom()
        val intent = Intent(
            this, MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            this, notificationNumber /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
        )
        val title = message.notification?.title
        val messages = message.notification?.body
        showNotification(title, messages, pendingIntent)
        Log.d("FCM", "Key Data : $title");
        Log.d("FCM", "Key Data : $messages");
    }
    private fun showNotification(
        title: String?,
        messageBody: String?,
        pendingIntent: PendingIntent
    ) {
        val channelId = "default_notification_channel_id"
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationNumber = generateRandom()
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            assert(notificationManager != null)
            notificationManager.createNotificationChannel(channel)
        }
        assert(notificationManager != null)
        notificationManager.notify(
            notificationNumber /* ID of notification */,
            notificationBuilder.build()
        )
    }

    private fun generateRandom(): Int {
        val random = Random()
        return random.nextInt(9999 - 1000) + 1000
    }
}