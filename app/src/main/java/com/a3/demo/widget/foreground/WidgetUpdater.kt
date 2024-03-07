package com.a3.demo.widget.foreground

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WidgetUpdater(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(
    context,
    params
) {
    override suspend fun doWork(): Result {
        if (instances > 0) {
            Log.d(TAG, "Another instance of WidgetUpdater is already running")
            return Result.success()
        }
        instances++
        var i = 0
        while (i <= 900) {
            Log.d(TAG, "WidgetUpdater instances: $instances")
            isWidgetUpdating = true
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, CounterWidget::class.java)
            )

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
            i++
            Thread.sleep(1000)
        }
        isWidgetUpdating = false
        instances--
        return Result.success()
    }

    companion object {
        private const val TAG = "WidgetUpdater"
        var isWidgetUpdating = false
        var instances: Int = 0
    }


}