package com.a3.demo.widget.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


class MyService : Service() {
    init {
        Log.d(TAG, "constructor called")
        isServiceRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate called")
        createNotificationChannel()
        isServiceRunning = true
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Service is Running")
            .setContentText("Listening for Screen Off/On events")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        if (!WidgetUpdater.isWidgetUpdating) {
            val UNIQUE_WORK_NAME = "WidgetUpdater"
            val workManager = WorkManager.getInstance(this)
            val request = PeriodicWorkRequest.Builder(
                WidgetUpdater::class.java,
                15,
                TimeUnit.MINUTES
            )
                .build()
            workManager.enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val appName = getString(R.string.app_name)
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            appName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        isServiceRunning = false
        stopForeground(true)

        // call MyReceiver which will restart this service via a worker
        val broadcastIntent = Intent(this, MyReceiver::class.java)
        sendBroadcast(broadcastIntent)
        super.onDestroy()
    }

    companion object {
        var isServiceRunning: Boolean = false
        private const val TAG = "MyService"
        private const val CHANNEL_ID = "NOTIFICATION_CHANNEL"
    }
}