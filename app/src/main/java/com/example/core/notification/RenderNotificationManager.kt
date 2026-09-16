package com.example.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RenderNotificationManager @Inject constructor(
    private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "render_channel"
        const val NOTIFICATION_ID = 2001
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "عمليات الرندر",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات توليد الصور والصوت والرندر"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun createNotification(title: String, progress: Int) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_media_play)
        .setContentTitle(title)
        .setContentText("التقدم: $progress%")
        .setProgress(100, progress, false)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .build()

    fun createCompletedNotification() = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_media_play)
        .setContentTitle("اكتمل الرندر")
        .setContentText("الفيديو جاهز للمعاينة")
        .setOngoing(false)
        .build()

    fun notifyCompleted() {
        try {
            if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, createCompletedNotification())
            }
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
